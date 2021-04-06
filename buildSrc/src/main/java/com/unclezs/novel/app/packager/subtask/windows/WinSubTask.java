package com.unclezs.novel.app.packager.subtask.windows;

import com.unclezs.novel.app.packager.packager.WindowsPackager;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.SignerHelper;
import java.io.File;

/**
 * windows的生成器基类，包含签名功能
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
public abstract class WinSubTask extends BaseSubTask {

  private static final String TIMESTAMPING_AUTHORITY = "http://timestamp.comodoca.com/authenticode";

  public WinSubTask(String artifactName) {
    super(artifactName);
  }

  protected void sign(File file, WindowsPackager packager) {
    if (packager.getWinConfig().getSigning() == null) {
      return;
    }
    Logger.infoIndent("Signing " + file);
    File keystore = packager.getWinConfig().getSigning().getKeystore();
    File certfile = packager.getWinConfig().getSigning().getCertfile();
    File keyfile = packager.getWinConfig().getSigning().getKeyfile();
    String alg = packager.getWinConfig().getSigning().getAlg();
    String storetype = packager.getWinConfig().getSigning().getStoretype();
    String storepass = packager.getWinConfig().getSigning().getStorepass();
    String alias = packager.getWinConfig().getSigning().getAlias();
    String keypass = packager.getWinConfig().getSigning().getKeypass();
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
      helper.tsaurl(TIMESTAMPING_AUTHORITY);
      helper.sign(file);
      Logger.infoUnIndent(file + " successfully signed!");
    } catch (Exception e) {
      Logger.errorUnIndent(file + " could not be signed", e);
    }

  }

}
