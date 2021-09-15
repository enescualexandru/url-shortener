package com.shortener.data.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    var id: Long?,
    val name: String,
    private val username: String,
    @JsonIgnore
    private val password: String,
    private var authorities: Collection<GrantedAuthority>
) : UserDetails {
    val serialVersionUID = 1L

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        @JvmStatic
        fun build(user: User): UserPrincipal {
            return UserPrincipal(
                user.id,
                user.name,
                user.email,
                user.password,
                arrayListOf(SimpleGrantedAuthority(user.role.name))
            )
        }
    }
}
