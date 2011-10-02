require 'rexml/document'

class PolicyPrinter
  INDENT_VALUE = "  "
  LINE_DELIMITER = "\n"
  
  def print_policys(pattern)
    puts "= Printing Models whose xml files match #{pattern}"
    Dir.glob("#{pattern}").each do |file_name|
      puts "== Processing #{file_name}"
      puts process_policy_file(file_name)
    end
  end
  
  def process_policy_file(file_name)
    output = ""
    file = File.new(file_name)
    doc = REXML::Document.new file
    root = doc.root
    
    # Gather the Top-level elements of the Policy
    output = add_line_to_output(output, 0, "Name: ", root.elements["Name"].text)
    output = add_line_to_output(output, 0, "Description: ", root.elements["Description"].text)
    output = add_line_to_output(output, 0, "Checkpoint: ", root.elements["RunTime"].text)
    output = add_line_to_output(output, 0, "Status: ", root.elements["Status"].text)
    
    # Gather the rules
    output = add_line_to_output(output, 0, "Rules: ", "")
    rules = Array.new
    rules = root.elements["RuleList"]
    for rule in rules do
      output = add_line_to_output(output, 1, "Name: ", rule.elements["Name"].text)
      output = add_line_to_output(output, 1, "Description: ", rule.elements["Notes"].text)
      output = add_line_to_output(output, 1, "Action Group: ", rule.elements["ActionGroup"].text)
      output = add_line_to_output(output, 1, "Alert Group: ", rule.elements["AlertGroup"].text)
      output = add_line_break_to_output(output)
    end
    
    return output
  end
  
  def self.print_usage
    puts "Usage: ruby print_policys.rb \"*policy*.xml\""
  end
  
  private
  
  def add_line_to_output(output, indent, name, value)
    if output != nil && indent != nil && name != nil && value != nil
      indents = ""
      indent.times {indents += INDENT_VALUE}
      output += "#{indents}#{name} #{value}#{LINE_DELIMITER}"
    end
    return output
  end
  
  def add_line_break_to_output(output)
    return output += "\n"
  end
end

if ARGV.length == 1
  pattern = ARGV[0]
  printer = PolicyPrinter.new()
  printer.print_policys(pattern)
else
  PolicyPrinter.print_usage()
end
