package vn.nhom14.qlns.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

final class AccountPrincipal extends User {
    private final String credentialVersion;
    AccountPrincipal(String username, String passwordHash, boolean enabled, String role) {
        super(username, passwordHash, enabled, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        credentialVersion = version(passwordHash);
    }
    boolean hasCredentialVersion(String passwordHash) { return credentialVersion.equals(version(passwordHash)); }
    private static String version(String hash) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(hash.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is required by the JVM", impossible);
        }
    }
}
