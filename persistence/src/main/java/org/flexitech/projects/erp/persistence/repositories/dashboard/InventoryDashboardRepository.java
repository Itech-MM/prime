package org.flexitech.projects.erp.persistence.repositories.dashboard;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.flexitech.projects.erp.persistence.models.inventory.LowStockItemModel;
import org.flexitech.projects.erp.persistence.models.inventory.StockMovementModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryDashboardRepository {

	private static final int STATUS_ACTIVE = 1;
	private static final int DOC_STATUS_POSTED = 4;

	private static final String ITEM_STOCK_TOTALS = "select i.id as item_id, i.code as code, i.name as name, "
			+ "i.reorder_level as reorder_level, coalesce(sum(b.qty_on_hand), 0) as on_hand "
			+ "from inv_item i left join inv_stock_balance b on b.item_id = i.id "
			+ "where i.status = ? group by i.id, i.code, i.name, i.reorder_level";

	private static final String LOW_STOCK_CONDITION = "t.on_hand > 0 and t.reorder_level is not null "
			+ "and t.reorder_level > 0 and t.on_hand <= t.reorder_level";

	private static final String OUT_OF_STOCK_CONDITION = "t.on_hand <= 0";

	private final JdbcTemplate jdbcTemplate;

	public InventoryDashboardRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public long countActiveItems() {
		Long value = this.jdbcTemplate.queryForObject("select count(*) from inv_item where status = ?", Long.class,
				STATUS_ACTIVE);
		return value == null ? 0L : value;
	}

	public long countActiveWarehouses() {
		Long value = this.jdbcTemplate.queryForObject("select count(*) from inv_warehouse where status = ?",
				Long.class, STATUS_ACTIVE);
		return value == null ? 0L : value;
	}

	public BigDecimal sumStockValue() {
		BigDecimal value = this.jdbcTemplate.queryForObject(
				"select coalesce(sum(qty_on_hand * avg_cost), 0) from inv_stock_balance", BigDecimal.class);
		return value == null ? BigDecimal.ZERO : value;
	}

	public long countLowStock() {
		return countFromTotals(LOW_STOCK_CONDITION);
	}

	public long countOutOfStock() {
		return countFromTotals(OUT_OF_STOCK_CONDITION);
	}

	private long countFromTotals(String condition) {
		Long value = this.jdbcTemplate.queryForObject(
				"select count(*) from (" + ITEM_STOCK_TOTALS + ") t where " + condition, Long.class, STATUS_ACTIVE);
		return value == null ? 0L : value;
	}

	public List<LowStockItemModel> findNeedsRestock(int limit) {
		String sql = "select t.code, t.name, t.on_hand, t.reorder_level from (" + ITEM_STOCK_TOTALS + ") t where ("
				+ LOW_STOCK_CONDITION + ") or (" + OUT_OF_STOCK_CONDITION + ") order by t.on_hand asc limit ?";
		return this.jdbcTemplate.query(sql,
				(rs, i) -> new LowStockItemModel(rs.getString("code"), rs.getString("name"),
						rs.getBigDecimal("on_hand"), rs.getBigDecimal("reorder_level")),
				STATUS_ACTIVE, limit);
	}

	public List<StockMovementModel> findRecentMovements(int limit) {
		String sql = "select l.posted_at, i.code, i.name, l.qty_in, l.qty_out, l.ref_doc_type "
				+ "from inv_stock_ledger l join inv_item i on i.id = l.item_id "
				+ "order by l.posted_at desc, l.id desc limit ?";
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
		return this.jdbcTemplate.query(sql, (rs, i) -> {
			Timestamp postedAt = rs.getTimestamp("posted_at");
			BigDecimal qtyIn = rs.getBigDecimal("qty_in") == null ? BigDecimal.ZERO : rs.getBigDecimal("qty_in");
			BigDecimal qtyOut = rs.getBigDecimal("qty_out") == null ? BigDecimal.ZERO : rs.getBigDecimal("qty_out");
			boolean incoming = qtyIn.compareTo(BigDecimal.ZERO) > 0;
			String refDocType = rs.getString("ref_doc_type");
			return new StockMovementModel(postedAt == null ? "-" : format.format(postedAt), rs.getString("code"),
					rs.getString("name"), incoming ? "IN" : "OUT", incoming ? qtyIn : qtyOut,
					refDocType == null ? "" : refDocType.replace('_', ' ').toLowerCase());
		}, limit);
	}

	public Map<String, BigDecimal> sumStockValueByCategory(int limit) {
		String sql = "select coalesce(c.name, 'Uncategorized') as category_name, "
				+ "coalesce(sum(b.qty_on_hand * b.avg_cost), 0) as value_total "
				+ "from inv_stock_balance b join inv_item i on i.id = b.item_id "
				+ "left join inv_item_category c on c.id = i.category_id "
				+ "group by coalesce(c.name, 'Uncategorized') order by value_total desc limit ?";
		return this.jdbcTemplate.query(sql, (ResultSetExtractor<Map<String, BigDecimal>>) rs -> {
			Map<String, BigDecimal> result = new LinkedHashMap<>();
			while (rs.next()) {
				result.put(rs.getString("category_name"), rs.getBigDecimal("value_total"));
			}
			return result;
		}, limit);
	}

	public BigDecimal sumPostedReceipts(Date start, Date end) {
		return sumPostedDocuments("inv_goods_receipt", start, end);
	}

	public BigDecimal sumPostedIssues(Date start, Date end) {
		return sumPostedDocuments("inv_goods_issue", start, end);
	}

	private BigDecimal sumPostedDocuments(String table, Date start, Date end) {
		String sql = "select coalesce(sum(total_amount), 0) from " + table
				+ " where status = ? and posted_time >= ? and posted_time < ?";
		BigDecimal value = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, DOC_STATUS_POSTED,
				new Timestamp(start.getTime()), new Timestamp(end.getTime()));
		return value == null ? BigDecimal.ZERO : value;
	}

	public List<MovementBucket> findMovementBuckets(Date start, Date end, boolean daily) {
		String format = daily ? "%Y%m%d" : "%Y%m";
		String sql = "select date_format(posted_at, '" + format + "') as bucket, "
				+ "coalesce(sum(qty_in), 0) as total_in, coalesce(sum(qty_out), 0) as total_out "
				+ "from inv_stock_ledger where posted_at >= ? and posted_at < ? "
				+ "group by date_format(posted_at, '" + format + "')";
		return this.jdbcTemplate.query(sql,
				(rs, i) -> new MovementBucket(rs.getString("bucket"), rs.getBigDecimal("total_in"),
						rs.getBigDecimal("total_out")),
				new Timestamp(start.getTime()), new Timestamp(end.getTime()));
	}

	public static class MovementBucket {

		public final String bucket;
		public final BigDecimal qtyIn;
		public final BigDecimal qtyOut;

		public MovementBucket(String bucket, BigDecimal qtyIn, BigDecimal qtyOut) {
			this.bucket = bucket;
			this.qtyIn = qtyIn == null ? BigDecimal.ZERO : qtyIn;
			this.qtyOut = qtyOut == null ? BigDecimal.ZERO : qtyOut;
		}
	}
}