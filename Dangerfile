require 'json'

sarif_path = 'build/reports/detekt/detekt.sarif'
if File.exist?(sarif_path)
  sarif = JSON.parse(File.read(sarif_path))

  results = sarif["runs"].flat_map { |run| run["results"] || [] }

  results.each do |result|
    msg = result["message"]["text"]
    file_path = result.dig("locations", 0, "physicalLocation", "artifactLocation", "uri")
    start_line = result.dig("locations", 0, "physicalLocation", "region", "startLine")

    if file_path && start_line && msg
      markdown("**Detekt** issue in `#{file_path}` at line #{start_line}:\n> #{msg}")
      warn(msg, file: file_path, line: start_line)
    end
  end
else
  warn("No Detekt SARIF file found at #{sarif_path}")
end
