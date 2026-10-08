package carisma.rt.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.gravity.security.annotations.requirements.Secrecy;
import org.junit.jupiter.api.Test;

class RTAgentSecrecyIntegrationTest {

	static final class SecretService {
		@Secrecy
		String secret() {
			return "secret";
		}

		@Secrecy(earlyReturn = "\"redacted\"")
		String secretWithEarlyReturn() {
			return "secret";
		}
	}

	static final class PublicService {
		String accessSecret() {
			return new SecretService().secret();
		}

		String accessSecretWithEarlyReturn() {
			return new SecretService().secretWithEarlyReturn();
		}
	}

	static final class SecretCaller {
		@Secrecy
		String accessSecret() {
			return new SecretService().secret();
		}
	}

	@Test
	void rejectsAccessFromCallerWithoutSecrecy() {
		assertThrows(SecurityException.class, () -> new PublicService().accessSecret());
	}

	@Test
	void permitsAccessFromCallerProvidingSecrecy() {
		assertEquals("secret", new SecretCaller().accessSecret());
	}

	@Test
	void usesConfiguredEarlyReturnForUnauthorizedAccess() {
		assertEquals("redacted", new PublicService().accessSecretWithEarlyReturn());
	}
}
