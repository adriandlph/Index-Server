#!/bin/bash

cat << "EOF"
 ╔══════════════════════════════════╗
 ║       Index Server Maven         ║
 ╚══════════════════════════════════╝
EOF

echo ""
echo "  1) Clean"
echo "  2) Build"
echo "  3) Package"
echo "  4) Start server"
echo "  5) Execute tests"
echo "  6) Generate keystore for SSL (HTTPS)"
echo ""
echo "════════════════════════════════"
read -p "  Select an option (1-6): " option

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
    mkdir -p dist
    cp target/*.jar dist/
    echo "JAR copied to dist/"
    ;;
  4)
    echo "Running: mvn spring-boot:run"
    mvn spring-boot:run
    ;;
  5)
    echo "Running: mvn test"
    mvn test
    ;;
  6)
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
