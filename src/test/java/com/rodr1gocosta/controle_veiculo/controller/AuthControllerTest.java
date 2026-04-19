package com.rodr1gocosta.controle_veiculo.controller;

import com.rodr1gocosta.controle_veiculo.dto.LoginRequest;
import com.rodr1gocosta.controle_veiculo.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("deve retornar token JWT quando login é bem-sucedido")
    void login_deveRetornarTokenQuandoCredenciaisValidas() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "admin123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponse response = authController.login(request);

        // Assert
        assertThat(response.token()).isEqualTo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...");
        assertThat(response.expiresIn()).isEqualTo(28800L); // 8 horas = 28800 segundos
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("deve lançar BadCredentialsException quando credenciais são inválidas")
    void login_deveLancarExcecaoQuandoCredenciaisInvalidas() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "senha_errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        assertThatThrownBy(() -> authController.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("deve gerar token com roles sem prefixo ROLE_")
    void login_deveGerarTokenComRolesSemPrefixo() {
        // Arrange
        LoginRequest request = new LoginRequest("user", "user123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token.user.jwt");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponse response = authController.login(request);

        // Assert
        verify(jwtEncoder).encode(argThat(params -> {
            JwtClaimsSet claims = params.getClaims();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.getClaim("roles");
            return roles.contains("USER") && !roles.contains("ROLE_USER");
        }));
    }

    @Test
    @DisplayName("deve gerar token com expiração de 8 horas")
    void login_deveGerarTokenComExpiracao8Horas() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "admin123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponse response = authController.login(request);

        // Assert
        assertThat(response.expiresIn()).isEqualTo(28800L); // 8 * 3600
        verify(jwtEncoder).encode(argThat(params -> {
            JwtClaimsSet claims = params.getClaims();
            Instant issuedAt = claims.getIssuedAt();
            Instant expiresAt = claims.getExpiresAt();
            return expiresAt != null && issuedAt != null;
        }));
    }
}