#编译 及 更新到云端 app打开后可以获取到更新
mvn clean package -P release,windows -U -X -DskipTests
mvn exec:exec@deploy-app -P release,windows
