require 'json'
require 'nokogiri'
require 'set'

# Global counters
$detekt_warnings = 0
$detekt_errors = 0
$lint_issues = 0

# Process Detekt SARIF
def process_detekt_sarif(path)
  unless File.exist?(path)
    warn("⚠️ No Detekt SARIF report found at `#{path}`")
    return
  end

  sarif = JSON.parse(File.read(path))
  results = sarif["runs"].flat_map { |run| run["results"] || [] }

  markdown("### 🔍 Detekt Issues Found: #{results.size}")

  results.each do |result|
    message_text = result.dig("message", "text")
    file = result.dig("locations", 0, "physicalLocation", "artifactLocation", "uri")
    line = result.dig("locations", 0, "physicalLocation", "region", "startLine") || 1
    severity = result["level"] || "warning"

    emoji = case severity
            when "error" then "❌"
            when "warning" then "⚠️"
            else "ℹ️"
            end

    markdown(<<~MD)
      <details>
        <summary>#{emoji} **Detekt (#{severity.capitalize})** in `#{file}` at line #{line}</summary>

        ```
        #{message_text}
        ```

        **File:** `#{file}`
        **Line:** #{line}
        **Severity:** `#{severity}`
      </details>
    MD

    case severity
    when "error"
      $detekt_errors += 1
      fail(message_text, file: file, line: line)
    when "warning"
      $detekt_warnings += 1
      warn(message_text, file: file, line: line)
    else
      message(message_text, file: file, line: line)
    end
  end
end

# Process Android Lint XML (deduplicated)
def process_android_lint(path)
  unless File.exist?(path)
    warn("⚠️ No Android Lint XML report found at `#{path}`")
    return
  end

  doc = Nokogiri::XML(File.read(path))
  issues = doc.xpath("//issue")

  seen = Set.new
  unique_issues = []

  issues.each do |issue|
    id = issue["id"]
    message_text = issue["message"]
    file = issue["file"] || issue.at_xpath('.//location')&.[]("file")
    line = (issue["line"] || issue.at_xpath('.//location')&.[]("line") || "1").to_i
    key = [id, message_text, file, line].join('|')
    next if seen.include?(key)

    seen << key
    unique_issues << issue
  end

  markdown("### 📱 Android Lint Issues Found: #{unique_issues.size}")

  unique_issues.each do |issue|
    file = issue["file"] || issue.at_xpath('.//location')&.[]("file")
    line = (issue["line"] || issue.at_xpath('.//location')&.[]("line") || "1").to_i
    id = issue["id"]
    message_text = issue["message"]
    severity = issue["severity"] || "warning"

    emoji = case severity.downcase
            when "error" then "❌"
            when "warning" then "⚠️"
            else "ℹ️"
            end

    markdown(<<~MD)
      <details>
        <summary>#{emoji} **Lint (#{severity.capitalize})** in `#{file}` at line #{line}</summary>

        ```
        #{message_text}
        ```

        **ID:** `#{id}`
        **File:** `#{file}`
        **Line:** #{line}
      </details>
    MD

    $lint_issues += 1
    warn(message_text, file: file, line: line)
  end
end

# Run Detekt and Lint processors
process_detekt_sarif('build/reports/detekt/merged-detekt-report.sarif')
process_android_lint('build/reports/lint/merged-lint-report.xml')