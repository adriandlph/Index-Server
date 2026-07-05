#!/bin/bash

cat << "EOF"
 ╔══════════════════════════════════╗
 ║       Index Server Maven         ║
 ╚══════════════════════════════════╝
EOF

echo ""
echo "  1) clean"
echo "  2) build"
echo "  3) package"
echo "  4) run"
echo "  5) generate-keystore"
echo ""
echo "════════════════════════════════"
read -p "  Select an option (1-5): " option

case $option in
  1)
    echo "Running: mvn clean"
    mvn clean
    ;;
  2)
    echo "Running: mvn clean compile"
    mvn clean compile
    ;;
  3)
    echo "Running: mvn clean package"
    mvn clean package
    ;;
  4)
    echo "Running: mvn spring-boot:run"
    mvn spring-boot:run
    ;;
  5)
    echo "Generating SSL keystore..."
    if [ -f secrets/ssl-key.p12 ]; then
      echo "A keystore already exists in secrets/"
      read -p "Do you want to overwrite it? (y/N): " confirm
      if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        echo "Aborted"
        exit 0
      fi
    fi
    mkdir -p secrets
    keytool -genkeypair -alias ssl-key -keyalg RSA -keysize 2048 \
      -storetype PKCS12 -keystore secrets/ssl-key.p12 -validity 365 \
      -storepass changeit -dname "CN=localhost, OU=Index, O=Internal, L=Localhost, ST=NA, C=NA"
    echo "Keystore generated at secrets/ssl-key.p12"
    ;;
  *)
    echo "Invalid option"
    exit 1
    ;;
esac
