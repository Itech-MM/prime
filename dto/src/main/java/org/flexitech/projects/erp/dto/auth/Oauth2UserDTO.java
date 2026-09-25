package org.flexitech.projects.erp.dto.auth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Oauth2UserDTO extends DefaultOAuth2User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7240058379282801247L;
	
	private final Long userId;
	private boolean needsPhone;
	private final String username;

	public Oauth2UserDTO(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
			String nameAttributeKey,
			 Long userId,
             boolean needsPhone, String username) {
		super(authorities, attributes, nameAttributeKey);
		this.userId = userId;
		this.needsPhone = needsPhone;
		this.username = username;
	}
	
	@Override
	public String getName() {
		return username;
	}

}
