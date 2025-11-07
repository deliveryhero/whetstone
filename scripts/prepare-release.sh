#!/bin/bash

# Whetstone Release Preparation Script
# This script validates your local environment and helps prepare a release

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo ""
echo "🔍 Whetstone Release Preparation"
echo "=================================="
echo ""

# Track if we have any errors
HAS_ERRORS=false

# Check 1: Are we on the main branch?
echo -n "✓ Checking if on main branch... "
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo -e "${RED}FAIL${NC}"
    echo "  Current branch: $CURRENT_BRANCH"
    echo "  You should be on the main branch to create a release"
    HAS_ERRORS=true
else
    echo -e "${GREEN}OK${NC}"
fi

# Check 2: Is working tree clean?
echo -n "✓ Checking for uncommitted changes... "
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}FAIL${NC}"
    echo "  You have uncommitted changes. Commit or stash them first."
    git status --short
    HAS_ERRORS=true
else
    echo -e "${GREEN}OK${NC}"
fi

# Check 3: Are we up to date with remote?
echo -n "✓ Checking if local is up to date with remote... "
git fetch origin main --quiet
LOCAL_COMMIT=$(git rev-parse main)
REMOTE_COMMIT=$(git rev-parse origin/main)
if [ "$LOCAL_COMMIT" != "$REMOTE_COMMIT" ]; then
    echo -e "${RED}FAIL${NC}"
    echo "  Your local main is not in sync with origin/main"
    echo "  Run: git pull origin main"
    HAS_ERRORS=true
else
    echo -e "${GREEN}OK${NC}"
fi

# Check 4: Is version a SNAPSHOT?
echo -n "✓ Checking current version... "
CURRENT_VERSION=$(grep "^VERSION_NAME=" gradle.properties | cut -d'=' -f2)
if [[ "$CURRENT_VERSION" != *SNAPSHOT* ]]; then
    echo -e "${YELLOW}WARNING${NC}"
    echo "  Current version is: $CURRENT_VERSION"
    echo "  Expected a SNAPSHOT version. Are you in the middle of a release?"
else
    echo -e "${GREEN}OK${NC} (current: $CURRENT_VERSION)"
fi

# Check 5: Can we build successfully?
echo -n "✓ Running tests... "
if ./gradlew build --quiet > /tmp/whetstone-release-build.log 2>&1; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}FAIL${NC}"
    echo "  Build failed. Check logs at: /tmp/whetstone-release-build.log"
    echo "  Last 20 lines:"
    tail -20 /tmp/whetstone-release-build.log
    HAS_ERRORS=true
fi

echo ""
echo "=================================="

if [ "$HAS_ERRORS" = true ]; then
    echo -e "${RED}❌ Pre-release checks failed!${NC}"
    echo ""
    echo "Please fix the issues above before creating a release."
    exit 1
fi

# All checks passed
echo -e "${GREEN}✅ All checks passed!${NC}"
echo ""

# Suggest a version based on current SNAPSHOT
if [[ "$CURRENT_VERSION" == *SNAPSHOT* ]]; then
    RELEASE_VERSION=$(echo "$CURRENT_VERSION" | sed 's/-SNAPSHOT//')
    echo -e "${BLUE}📦 Ready to release version: ${YELLOW}$RELEASE_VERSION${NC}"
    echo ""
    echo "Next steps:"
    echo ""
    echo -e "${YELLOW}1. Update gradle.properties${NC}"
    echo "   sed -i.bak 's/VERSION_NAME=.*/VERSION_NAME=$RELEASE_VERSION/' gradle.properties && rm gradle.properties.bak"
    echo ""
    echo -e "${YELLOW}2. Commit the release version${NC}"
    echo "   git commit -am \"Prepare for release $RELEASE_VERSION\""
    echo "   git push origin main"
    echo ""
    echo -e "${YELLOW}3. Create GitHub Release${NC}"
    echo "   Go to: https://github.com/deliveryhero/whetstone/releases/new"
    echo "   - Tag: v$RELEASE_VERSION (create new tag)"
    echo "   - Title: v$RELEASE_VERSION"
    echo "   - Description: Write release notes"
    echo "   - Click 'Publish release'"
    echo ""
    echo "The workflow will automatically:"
    echo "  ✓ Validate VERSION_NAME in gradle.properties"
    echo "  ✓ Run tests"
    echo "  ✓ Publish to Maven Central"
    echo "  ✓ Commit next SNAPSHOT version to main"
    echo ""
else
    echo "Current version: $CURRENT_VERSION"
    echo ""
    echo "It looks like you may have already updated the version."
    echo "Next steps:"
    echo ""
    echo "1. Commit and push if not already done:"
    echo "   git commit -am \"Prepare for release $CURRENT_VERSION\""
    echo "   git push origin main"
    echo ""
    echo "2. Create GitHub Release:"
    echo "   Go to: https://github.com/deliveryhero/whetstone/releases/new"
    echo "   Write release notes and publish"
    echo ""
fi

exit 0
