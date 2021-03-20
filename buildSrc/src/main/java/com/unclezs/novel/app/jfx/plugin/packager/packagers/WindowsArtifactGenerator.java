package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.utils.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.utils.SignerException;
import com.unclezs.novel.app.jfx.plugin.packager.utils.SignerHelper;

import java.io.File;

/**
 * Artifact generation base class including Windows specific features (signing)
 */
public abstract class WindowsArtifactGenerator extends ArtifactGenerator {

	private static final String TIMESTAMPING_AUTHORITY = "http://timestamp.comodoca.com/authenticode";

	public WindowsArtifactGenerator(String artifactName) {
		super(artifactName);
	}

	protected void sign(File file, WindowsPackager packager) {
		if (packager.getWinConfig().getSigning() == null)
			return;

		Logger.infoIndent("Signing " + file);

		File keystore = packager.getWinConfig().getSigning().getKeystore();
		File certfile = packager.getWinConfig().getSigning().getCertfile();
		File keyfile = packager.getWinConfig().getSigning().getKeyfile();
		String alg = packager.getWinConfig().getSigning().getAlg();
		String storetype = packager.getWinConfig().getSigning().getStoretype();
		String storepass = packager.getWinConfig().getSigning().getStorepass();
		String alias = packager.getWinConfig().getSigning().getAlias();
		String keypass = packager.getWinConfig().getSigning().getKeypass();
		String tsa = TIMESTAMPING_AUTHORITY;
		String displayName = packager.getDisplayName();
		String url = packager.getUrl();

		try {

			SignerHelper helper = new SignerHelper();
			helper.name(displayName);
			helper.url(url);
			helper.alg(alg);
			helper.keystore(keystore);
			helper.storepass(storepass);
			helper.storetype(storetype);
			helper.alias(alias);
			helper.certfile(certfile);
			helper.keyfile(keyfile);
			helper.keypass(keypass);
			helper.tsaurl(tsa);
			helper.sign(file);

			Logger.infoUnindent(file + " successfully signed!");

		} catch (SignerException e) {

			Logger.errorUnindent(file + " could not be signed", e);

		}

	}

}
