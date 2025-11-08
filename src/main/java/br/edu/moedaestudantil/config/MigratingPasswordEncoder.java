package br.edu.moedaestudantil.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder que suporta migração de senhas antigas em texto puro.
 * Permite login com senhas em texto puro (para compatibilidade) e senhas BCrypt.
 */
public class MigratingPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bcryptEncoder;
    
    public MigratingPasswordEncoder() {
        this.bcryptEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        // Sempre criptografa com BCrypt para novas senhas
        return bcryptEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Se a senha codificada começa com $2a$, $2b$ ou $2y$, está em BCrypt
        if (isBCryptEncoded(encodedPassword)) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        } else {
            // Senha em texto puro - comparar diretamente (compatibilidade com dados antigos)
            return rawPassword.toString().equals(encodedPassword);
        }
    }
    
    private boolean isBCryptEncoded(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}

