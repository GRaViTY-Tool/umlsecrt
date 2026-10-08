package carisma.rt.instrument;

import org.gravity.security.annotations.requirements.Secrecy;

public final class RTAgentTestApplication {

	static final class SecretService {
		@Secrecy
		String secret() { return "secret"; }

		@Secrecy(earlyReturn = "\"redacted\"")
		String secretWithEarlyReturn() { return "secret"; }
	}

	static final class PublicService {
		String accessSecret() { return new SecretService().secret(); }
		String accessSecretWithEarlyReturn() { return new SecretService().secretWithEarlyReturn(); }
	}

	static final class SecretCaller {
		@Secrecy
		String secret() { return "secret"; }

		String accessSecret() { return secret(); }
	}

	public static void main(String[] args) {
		boolean rejected = false;
		try {
			new PublicService().accessSecret();
		} catch (SecurityException expected) {
			rejected = true;
		}
		if (!rejected) {
			throw new AssertionError("Unannotated caller was allowed to access @Secrecy method");
		}
		if (!"secret".equals(new SecretCaller().accessSecret())) {
			throw new AssertionError("@Secrecy caller could not access @Secrecy method");
		}
		if (!"redacted".equals(new PublicService().accessSecretWithEarlyReturn())) {
			throw new AssertionError("Configured secrecy early return was not used");
		}
	}
}
