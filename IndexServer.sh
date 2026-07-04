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
echo ""
echo "════════════════════════════════"
read -p "  Select an option (1-4): " option

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
  *)
    echo "Invalid option"
    exit 1
    ;;
esac
