package webservicesapi.command.impl;

import webservicesapi.command.Command;
import webservicesapi.command.CommandSet;
import webservicesapi.command.InvalidCommandException;
import webservicesapi.output.Output;
import webservicesapi.output.OutputQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple calculator.
 * 
 * See http://www.jdocs.com/math/1.1/overview-summary.html
 *
 * @author Ben Leov
 */
public class CalcCommandSet implements CommandSet {

    @Override
    public Set<Command> getCommands() {
        HashSet<Command> commands = new HashSet<Command>();
        commands.add(new CommandBase() {

            @Override
            public String[] getRequiredProperties() {
                return null;
            }

            public String getCommandName() {
                return "calc";
            }

            @Override
            public void processCommand(String command, String parameter, OutputQueue queue)
                    throws InvalidCommandException {

                if (parameter == null) {
                    throw new InvalidCommandException("Please enter an equation to calculate.");
                } else {

                    String[] params = parameter.split(" ");

                    Float total = getNumber(params[0]);

                    for (int x = 0; x < params.length; x += 2) {

                        if (params.length > x + 2) {
                            total = calculate(total, params[x + 1], getNumber(params[x + 2]));
                        } else {
                            break;
                        }
                    }

                    if (total == null) {
                        queue.send(new Output(this, "Unknown"));
                    } else {
                        queue.send(new Output(this, "= " + total.toString()));
                    }
                }
            }

            @Override
            public String getUsage() {
                return "<equation>";
            }

            public Float calculate(Float first, String sign, Float second) {

                if (first == null || second == null) {
                    return null;
                } else {

                    if (sign.equals("+") || sign.equals("plus")) {
                        return first + second;
                    } else if (sign.equals("-") || sign.equals("minus")) {
                        return first - second;
                    } else if (sign.equals("*") || sign.equals("times") || sign.equals("multiply")) {
                        return first * second;
                    } else if(sign.equals("/") || sign.equals("divide")){
                    	return first / second;
                    } else if(sign.equals("%") || sign.equals("mod") || sign.equals("modulus")){
                    	return first % second; 

                    }
                    else if (sign.equals("Rand")){
                    	return returnRandom(first, second);
                    }
                    return null;
                }
            }

            public float returnRandom(Float min, Float max) {
            		return 1;
            }
            
            public Float getNumber(String number) {

                if (number != null) {

                    StringBuilder cleaned = new StringBuilder();
                    int decCount = 0;

                    for (char curr : number.toCharArray()) {
                        if (Character.isDigit(curr)) {
                            cleaned.append(curr);
                        } else if (curr == '.' && decCount == 0) {
                            cleaned.append('.');
                            ++decCount;
                        }
                    }
                    try {
                        Float f = Float.parseFloat(cleaned.toString());
                        return f;
                    }
                    catch (NumberFormatException nfe) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            @Override
            public String getHelp() {
                return "A simple calculator";
            }
        });
        return commands;
    }
}