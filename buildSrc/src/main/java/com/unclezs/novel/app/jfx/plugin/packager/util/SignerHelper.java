package com.unclezs.novel.app.jfx.plugin.packager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.jsign.AuthenticodeSigner;
import net.jsign.DigestAlgorithm;
import net.jsign.KeyStoreUtils;
import net.jsign.PrivateKeyUtils;
import net.jsign.Signable;
import net.jsign.timestamp.TimestampingMode;

/**
 * Helper class to create AuthenticodeSigner instances with untyped parameters. This is used
 * internally to share the parameter validation logic between the Ant task and the CLI tool.
 *
 * @since 2.0
 */
public class SignerHelper {

  public static final String PARAM_KEYSTORE = "keystore";
  public static final String PARAM_STOREPASS = "storepass";
  public static final String PARAM_STORETYPE = "storetype";
  public static final String PARAM_ALIAS = "alias";
  public static final String PARAM_KEYPASS = "keypass";
  public static final String PARAM_KEYFILE = "keyfile";
  public static final String PARAM_CERTFILE = "certfile";
  public static final String PARAM_ALG = "alg";
  public static final String PARAM_TSAURL = "tsaurl";
  public static final String PARAM_TSMODE = "tsmode";
  public static final String PARAM_TSRETRIES = "tsretries";
  public static final String PARAM_TSRETRY_WAIT = "tsretrywait";
  public static final String PARAM_NAME = "name";
  public static final String PARAM_URL = "url";
  public static final String PARAM_PROXY_URL = "proxyUrl";
  public static final String PARAM_PROXY_USER = "proxyUser";
  public static final String PARAM_PROXY_PASS = "proxyPass";
  public static final String PARAM_REPLACE = "replace";
  public static final String PARAM_ENCODING = "encoding";

  private File keystore;
  private String storepass;
  private String storetype;
  private String alias;
  private String keypass;
  private File keyfile;
  private File certfile;
  private String tsaurl;
  private String tsmode;
  private int tsretries = -1;
  private int tsretrywait = -1;
  private String alg;
  private String name;
  private String url;
  private String proxyUrl;
  private String proxyUser;
  private String proxyPass;
  private boolean replace;
  private Charset encoding;

  public SignerHelper keystore(String keystore) {
    keystore(createFile(keystore));
    return this;
  }

  public SignerHelper keystore(File keystore) {
    this.keystore = keystore;
    return this;
  }

  public SignerHelper storepass(String storepass) {
    this.storepass = storepass;
    return this;
  }

  public SignerHelper storetype(String storetype) {
    this.storetype = storetype;
    return this;
  }

  public SignerHelper alias(String alias) {
    this.alias = alias;
    return this;
  }

  public SignerHelper keypass(String keypass) {
    this.keypass = keypass;
    return this;
  }

  public SignerHelper keyfile(String keyfile) {
    keyfile(createFile(keyfile));
    return this;
  }

  public SignerHelper keyfile(File keyfile) {
    this.keyfile = keyfile;
    return this;
  }

  public SignerHelper certfile(String certfile) {
    certfile(createFile(certfile));
    return this;
  }

  public SignerHelper certfile(File certfile) {
    this.certfile = certfile;
    return this;
  }

  public SignerHelper alg(String alg) {
    this.alg = alg;
    return this;
  }

  public SignerHelper tsaurl(String tsaurl) {
    this.tsaurl = tsaurl;
    return this;
  }

  public SignerHelper tsmode(String tsmode) {
    this.tsmode = tsmode;
    return this;
  }

  public SignerHelper tsretries(int tsretries) {
    this.tsretries = tsretries;
    return this;
  }

  public SignerHelper tsretrywait(int tsretrywait) {
    this.tsretrywait = tsretrywait;
    return this;
  }

  public SignerHelper name(String name) {
    this.name = name;
    return this;
  }

  public SignerHelper url(String url) {
    this.url = url;
    return this;
  }

  public SignerHelper proxyUrl(String proxyUrl) {
    this.proxyUrl = proxyUrl;
    return this;
  }

  public SignerHelper proxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
    return this;
  }

  public SignerHelper proxyPass(String proxyPass) {
    this.proxyPass = proxyPass;
    return this;
  }

  public SignerHelper replace(boolean replace) {
    this.replace = replace;
    return this;
  }

  public SignerHelper encoding(String encoding) {
    this.encoding = Charset.forName(encoding);
    return this;
  }

  public SignerHelper param(String key, String value) {
    if (value == null) {
      return this;
    }

    switch (key) {
      case PARAM_KEYSTORE:
        return keystore(value);
      case PARAM_STOREPASS:
        return storepass(value);
      case PARAM_STORETYPE:
        return storetype(value);
      case PARAM_ALIAS:
        return alias(value);
      case PARAM_KEYPASS:
        return keypass(value);
      case PARAM_KEYFILE:
        return keyfile(value);
      case PARAM_CERTFILE:
        return certfile(value);
      case PARAM_ALG:
        return alg(value);
      case PARAM_TSAURL:
        return tsaurl(value);
      case PARAM_TSMODE:
        return tsmode(value);
      case PARAM_TSRETRIES:
        return tsretries(Integer.parseInt(value));
      case PARAM_TSRETRY_WAIT:
        return tsretrywait(Integer.parseInt(value));
      case PARAM_NAME:
        return name(value);
      case PARAM_URL:
        return url(value);
      case PARAM_PROXY_URL:
        return proxyUrl(value);
      case PARAM_PROXY_USER:
        return proxyUser(value);
      case PARAM_PROXY_PASS:
        return proxyPass(value);
      case PARAM_REPLACE:
        return replace("true".equalsIgnoreCase(value));
      case PARAM_ENCODING:
        return encoding(value);
      default:
        throw new IllegalArgumentException("Unknown key " + key);
    }
  }

  private File createFile(String file) {
    return file == null ? null : new File(file);
  }

  private AuthenticodeSigner build() throws SignerException {
    PrivateKey privateKey;
    Certificate[] chain;

    // some exciting parameter validation...
    if (keystore == null && keyfile == null && certfile == null) {
      throw new SignerException("keystore, or keyfile and certfile must be set");
    }
    if (keystore != null && keyfile != null) {
      throw new SignerException("keystore can't be mixed with keyfile");
    }

    Provider provider = null;
    if ("PKCS11".equals(storetype)) {
      // the keystore parameter is either the provider name or the SunPKCS11 configuration file
      if (keystore != null && keystore.exists()) {
        provider = createSunPKCS11Provider(keystore);
      } else if (keystore != null && keystore.getName().startsWith("SunPKCS11-")) {
        provider = Security.getProvider(keystore.getName());
        if (provider == null) {
          throw new SignerException("Security provider " + keystore.getName() + " not found");
        }
      } else {
        throw new SignerException(
            "keystore should either refer to the SunPKCS11 configuration file or to the name of the provider configured in jre/lib/security/java.security");
      }
    }

    if (keystore != null) {
      KeyStore ks;
      Set<String> aliases = new LinkedHashSet<>();
      try {
        ks = KeyStoreUtils.load(keystore, storetype, storepass, provider);
        aliases.addAll(Collections.list(ks.aliases()));
        if (aliases.isEmpty()) {
          throw new KeyStoreException(
              "No certificate found in the keystore " + (provider != null ? provider.getName()
                  : keystore));
        }
      } catch (KeyStoreException e) {
        throw new SignerException(e.getMessage(), e);
      }

      if (alias == null) {
        if (aliases.size() == 1) {
          alias = aliases.iterator().next();
        } else {
          throw new SignerException(
              "alias must be set to select a certificate (available aliases: " + String
                  .join(", ", aliases) + ")");
        }
      }

      try {
        chain = ks.getCertificateChain(alias);
      } catch (KeyStoreException e) {
        throw new SignerException(e.getMessage(), e);
      }
      if (chain == null) {
        throw new SignerException(
            "No certificate found under the alias '" + alias + "' in the keystore " + (
                provider != null ? provider.getName() : keystore) + " (available aliases: " + String
                .join(", ", aliases) + ")");
      }
      if (certfile != null) {
        if (chain.length != 1) {
          throw new SignerException(
              "certfile can only be specified if the certificate from the keystore contains only one entry");
        }
        // replace the certificate chain from the keystore with the complete chain from file
        try {
          Certificate[] chainFromFile = loadCertificateChain(certfile);
          if (chainFromFile[0].equals(chain[0])) {
            // replace certificate with complete chain
            chain = chainFromFile;
          } else {
            throw new SignerException("The certificate chain in " + certfile
                + " does not match the chain from the keystore");
          }
        } catch (SignerException e) {
          throw e;
        } catch (Exception e) {
          throw new SignerException("Failed to load the certificate from " + certfile, e);
        }
      }

      char[] password = keypass != null ? keypass.toCharArray() : storepass.toCharArray();

      try {
        privateKey = (PrivateKey) ks.getKey(alias, password);
      } catch (Exception e) {
        throw new SignerException("Failed to retrieve the private key from the keystore", e);
      }

    } else {
      // separate private key and certificate files (PVK/SPC)
      if (keyfile == null) {
        throw new SignerException("keyfile must be set");
      }
      if (!keyfile.exists()) {
        throw new SignerException("The keyfile " + keyfile + " couldn't be found");
      }
      if (certfile == null) {
        throw new SignerException("certfile must be set");
      }
      if (!certfile.exists()) {
        throw new SignerException("The certfile " + certfile + " couldn't be found");
      }

      // load the certificate chain
      try {
        chain = loadCertificateChain(certfile);
      } catch (Exception e) {
        throw new SignerException("Failed to load the certificate from " + certfile, e);
      }

      // load the private key
      try {
        privateKey = PrivateKeyUtils.load(keyfile, keypass != null ? keypass : storepass);
      } catch (Exception e) {
        throw new SignerException("Failed to load the private key from " + keyfile, e);
      }
    }

    if (alg != null && DigestAlgorithm.of(alg) == null) {
      throw new SignerException("The digest algorithm " + alg + " is not supported");
    }

    try {
      initializeProxy(proxyUrl, proxyUser, proxyPass);
    } catch (Exception e) {
      throw new SignerException("Couldn't initialize proxy", e);
    }

    // configure the signer
    return new AuthenticodeSigner(chain, privateKey)
        .withProgramName(name)
        .withProgramURL(url)
        .withDigestAlgorithm(DigestAlgorithm.of(alg))
        .withSignatureProvider(provider)
        .withSignaturesReplaced(replace)
        .withTimestamping(tsaurl != null || tsmode != null)
        .withTimestampingMode(
            tsmode != null ? TimestampingMode.of(tsmode) : TimestampingMode.AUTHENTICODE)
        .withTimestampingRetries(tsretries)
        .withTimestampingRetryWait(tsretrywait)
        .withTimestampingAuthority(tsaurl != null ? tsaurl.split(",") : null);
  }

  /**
   * Create a SunPKCS11 provider with the specified configuration file.
   *
   * @param configuration the SunPKCS11 configuration file
   */
  private Provider createSunPKCS11Provider(File configuration) throws SignerException {
    try {
      try {
        // Java 9 and later, using the Provider.configure() method
        Method providerConfigureMethod = Provider.class.getMethod("configure", String.class);
        Provider provider = Security.getProvider("SunPKCS11");
        return (Provider) providerConfigureMethod.invoke(provider, configuration.getPath());
      } catch (NoSuchMethodException e) {
        // prior to Java 9, direct instantiation of the SunPKCS11 class
        Constructor<?> sunpkcs11Constructor = Class.forName("sun.security.pkcs11.SunPKCS11")
            .getConstructor(String.class);
        return (Provider) sunpkcs11Constructor.newInstance(configuration.getPath());
      }
    } catch (Exception e) {
      throw new SignerException(
          "Failed to create a SunPKCS11 provider from the configuration file " + configuration, e);
    }
  }

  public void sign(File file) throws SignerException {
    if (file == null) {
      throw new SignerException("file must be set");
    }
    if (!file.exists()) {
      throw new SignerException("The file " + file + " couldn't be found");
    }

    Signable signable;
    try {
      signable = Signable.of(file, encoding);
    } catch (UnsupportedOperationException e) {
      throw new SignerException(e.getMessage());
    } catch (IOException e) {
      throw new SignerException("Couldn't open the file " + file, e);
    }

    try {
      AuthenticodeSigner signer = build();
      Logger.info("Adding Authenticode signature to " + file);
      signer.sign(signable);
    } catch (SignerException e) {
      throw e;
    } catch (Exception e) {
      throw new SignerException("Couldn't sign " + file, e);
    }
  }

  /**
   * Load the certificate chain from the specified PKCS#7 files.
   */
  private Certificate[] loadCertificateChain(File file) throws IOException, CertificateException {
    try (FileInputStream in = new FileInputStream(file)) {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
      return certificates.toArray(new Certificate[0]);
    }
  }

  /**
   * Initializes the proxy.
   *
   * @param proxyUrl      the url of the proxy (either as hostname:port or http[s]://hostname:port)
   * @param proxyUser     the username for the proxy authentication
   * @param proxyPassword the password for the proxy authentication
   */
  private void initializeProxy(String proxyUrl, final String proxyUser, final String proxyPassword)
      throws MalformedURLException {
    // Do nothing if there is no proxy url.
    if (proxyUrl != null && proxyUrl.trim().length() > 0) {
      if (!proxyUrl.trim().startsWith("http")) {
        proxyUrl = "http://" + proxyUrl.trim();
      }
      final URL url = new URL(proxyUrl);
      final int port = url.getPort() < 0 ? 80 : url.getPort();

      ProxySelector.setDefault(new ProxySelector() {
        public List<Proxy> select(URI uri) {
          Proxy proxy;
          if (uri.getScheme().equals("socket")) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(url.getHost(), port));
          } else {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), port));
          }
          Logger.info("Proxy selected for " + uri + " : " + proxy);
          return Collections.singletonList(proxy);
        }

        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        }
      });

      if (proxyUser != null && proxyUser.length() > 0 && proxyPassword != null) {
        Authenticator.setDefault(new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
          }
        });
      }
    }
  }
}
