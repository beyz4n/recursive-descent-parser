import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LexicalAnalyser {

    // Variables to keep track of row and column values
    public static int row = 1;
    public static int col = 0;

    public static String tokens = ""; // Variable to keep all messages for tokens
    public static PrintWriter printer; // Declaration for PrintWriter in order to print to the output file
    public static File file; // File to put using constructor

    // Creating a constructor for the lexical part to call before doing syntax analysis
    public LexicalAnalyser(File input) throws FileNotFoundException {
        // putting the file given in the constructor to the actual one
        file = input;
        // calling the main to start the lexical analysis
        main();
    }

    public static void main() throws FileNotFoundException {

        // Create a file for output.
        File output = new File("lexical_output.txt");
        printer = new PrintWriter(output);

        // Scanner to read inside the given file
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) { // Check if input file has next line
            String line = scanner.nextLine(); // Variable to keep one line of the code
            col = 0;
            while (line.length() > col) { // Check if end of the line
                char currentCh = line.charAt(col); // Variable to keep character at that column
                if (currentCh == ' ') { // Eliminate spaces
                    col++;
                } else if (currentCh == '~') { // Don't read what's on that line after tilde sign (~)
                    col = 0;
                    break;

                // Detect brackets
                } else if (currentCh == '(') {
                    col++;
                    tokens += "LEFTPAR " + row + ":" + col + "\n";
                } else if (currentCh == ')') {
                    col++;
                    tokens += "RIGHTPAR " + row + ":" + col + "\n";
                } else if (currentCh == '[') {
                    col++;
                    tokens += "LEFTSQUAREB " + row + ":" + col + "\n";
                } else if (currentCh == ']') {
                    col++;
                    tokens += "RIGHTSQUAREB " + row + ":" + col + "\n";
                } else if (currentCh == '{') {
                    col++;
                    tokens += "LEFTCURLYB " + row + ":" + col + "\n";
                } else if (currentCh == '}') {
                    col++;
                    tokens += "RIGHTCURLYB " + row + ":" + col + "\n";

                // Detect identifiers or numbers (starting with dot(.), plus(+) or minus(-))
                } else if ((currentCh == '.' || currentCh == '+' || currentCh == '-')) {
                    String str = "" + currentCh;
                    int currentCol = col + 1;
                    // Take whole token until encounter a bracket or space
                    while (currentCol != line.length() && line.charAt(currentCol) != ' ' && line.charAt(currentCol) != '(' && line.charAt(currentCol) != ')' &&
                            line.charAt(currentCol) != '[' && line.charAt(currentCol) != ']' && line.charAt(currentCol) != '{' && line.charAt(currentCol) != '}' && line.charAt(currentCol) != '~') {
                        str += line.charAt(currentCol);
                        currentCol++;
                    }
                    // If it is just a single dot(.), plus(+) or minus(-), it is an identifier
                    if (str.length() == 1) {
                        tokens += "IDENTIFIER " + row + ":" + (col + 1) + "\n";
                        col++;
                    // Else it can be a number starting with dot(.), plus(+) or minus(-)
                    } else if ((currentCh == '+' || currentCh == '-') && (line.charAt(col + 1) == '.' || (line.charAt(col + 1) >= '0' && line.charAt(col + 1) <= '9'))) {
                        isNumber(line, currentCh);
                    } else if (currentCh == '.' && (line.charAt(col + 1) >= '0' && line.charAt(col + 1) <= '9')) {
                        isNumber(line, currentCh);
                    }
                    // Otherwise it is an invalid input, so print error message
                    else {
                        printErrorMessages(str);
                    }

                // Detect identifier & keyword & boolean
                } else if ((currentCh >= 'a' && currentCh <= 'z') || currentCh == '!' || currentCh == '*' || currentCh == '/' || currentCh == ':' || currentCh == '<' || currentCh == '>' || currentCh == '=' || currentCh == '?') {
                    // Check the lexeme is a keyword or not
                    if (!(isAKeyword(line, "true") || isAKeyword(line, "false") || isAKeyword(line, "define") || isAKeyword(line, "let") || isAKeyword(line, "cond") ||
                            isAKeyword(line, "if") || isAKeyword(line, "begin"))) {
                        // If not a keyword, it can be an identifier. Take the lexeme character by character until it encounters a space or a bracket or a tilde
                        String identifier = "" + line.charAt(col);
                        boolean isIdentifier = true;
                        int currentCol = col + 1;
                        while (currentCol != line.length() && line.charAt(currentCol) != ' ' && line.charAt(currentCol) != '(' && line.charAt(currentCol) != ')' &&
                                line.charAt(currentCol) != '[' && line.charAt(currentCol) != ']' && line.charAt(currentCol) != '{' && line.charAt(currentCol) != '}' && line.charAt(currentCol) != '~') {
                            identifier += line.charAt(currentCol);
                            currentCol++;
                        }
                        // Figure out if it's really identifier by checking the rules of being identifier
                        int currentcol = 1;
                        while ((identifier.length() - 1) >= currentcol) {
                            if ((identifier.charAt(currentcol) >= 'a' && identifier.charAt(currentcol) <= 'z') || (identifier.charAt(currentcol) >= '0' &&
                                    identifier.charAt(currentcol) <= '9') || (identifier.charAt(currentcol) == '.' || (identifier.charAt(currentcol) == '+' || (identifier.charAt(currentcol) == '-')))) {
                                currentcol++;
                            }
                            // If it is not an identifier, print error message
                            else {
                                isIdentifier = false;
                                printErrorMessages(identifier);
                            }
                        }
                        // If it is identifier add it to tokens String
                        if (isIdentifier) {
                            tokens += "IDENTIFIER " + row + ":" + (col + 1) + "\n";
                            col += identifier.length();
                        }
                    }
                // Detect characters
                } else if (currentCh == '\'') {
                    String chars; // Variable to keep char
                    int currentcol = col + 1;
                    boolean isAChar = false;

                    // Find the index of where the character ends
                    while (line.length() - 1 >= currentcol) {
                        if (line.indexOf('\'', col + 1) == -1)
                            // If there is no second apostrophe after the first one, break the loop.
                            break;
                        if (line.charAt(currentcol) == '\\') {
                            // If character is \', this can not end the character so increment currentcol by not 1 but 2.
                            currentcol += 2;
                        } else if (line.charAt(currentcol) == '\'') {
                            // If apostrophe for end of the character is found, break the loop.
                            isAChar = true;
                            break;
                        } else
                            currentcol++;
                    }

                    if (!isAChar) { // If it is not a character give an error and exit the system.
                        chars = line.substring(col);
                        printErrorMessages(chars);
                    }
                    // Figure out if it's really character by checking the rules of being character
                    chars = line.substring(col, currentcol + 1);
                    // If character is in ( 'a' ) format
                    if (chars.length() == 3 && chars.charAt(1) != '\\' && chars.charAt(1) != '\'' && chars.charAt(2) == '\'') {
                        tokens += "CHAR " + row + ":" + (col + 1) + "\n";
                        col += 3;
                    // If character is '\\' or '\''
                    } else if (chars.length() == 4 && chars.charAt(3) == '\'' && chars.charAt(1) == '\\' && (chars.charAt(2) == '\\' || chars.charAt(2) == '\'')) {
                        tokens += "CHAR " + row + ":" + (col + 1) + "\n";
                        col += 4;
                    // Else it is an invalid token, print error message and exit the system
                    } else {
                        printErrorMessages(chars);
                    }

                // Detect strings
                } else if (currentCh == '"') {
                    String str; // variable to keep string
                    int currentcol = col + 1;
                    boolean isAString = false;

                    // Find the index of where the string ends
                    while (line.length() - 1 >= currentcol) {
                        if (line.indexOf('"', col + 1) == -1)
                            // If there is no second quotation mark after the first one, break the loop.
                            break;
                        if (line.charAt(currentcol) == '\\') {
                            // If character is \", this can not end the string so increment currentcol by not 1 but 2.
                            currentcol += 2;
                        } else if (line.charAt(currentcol) == '"') {
                            // if quotation mark for end of the string is found, break the loop.
                            isAString = true;
                            break;
                        } else
                            currentcol++;
                    }

                    // If it is not a string give an error and exit the system.
                    if (!isAString) {
                        str = line.substring(col);
                        printErrorMessages(str);
                    }

                    // Figure out if it's really string by checking the rules of being string
                    str = line.substring(col, currentcol + 1);
                    // If its length greater than 2 -> if it is not ""
                    // and it finishes with quotation mark also does not contain any backslash, it is definitely a string
                    if (str.length() > 2 && str.charAt(str.length() - 1) == '"' && !str.contains("\\")) {
                        tokens += "STRING " + row + ":" + (col + 1) + "\n";
                        col += str.length();
                    }
                    // If it contains backslash, check all backslashes continue with a backslash or quotation mark
                    else if (str.length() > 2 && str.charAt(str.length() - 1) == '"' && str.contains("\\")) {
                        int count = 0;
                        boolean isValidString = true;
                        while (count != str.length() - 3) {
                            if (str.charAt(count) == '\\' && (str.charAt(count + 1) == '\\' || str.charAt(count + 1) == '\"')) {
                                count += 2;
                            } else if (str.charAt(count) != '\\') {
                                count++;
                            } else {
                                isValidString = false;
                                printErrorMessages(str);
                            }
                        }
                        // If it is a string add it to tokens String
                        if (isValidString) {
                            tokens += "STRING " + row + ":" + (col + 1) + "\n";
                            col += str.length();
                        }
                    }
                    else {
                        printErrorMessages(str);
                    }

                // Detect numbers
                } else if ((currentCh <= '9' && currentCh >= '0') || currentCh == '+' || currentCh == '-' || currentCh == '.') {
                    isNumber(line, currentCh);
                } else {
                    printErrorMessages("" + currentCh);
                }
            }
            row++; // Increment row at the end of each line
        }
        // If there is no invalid token, print all of them
    //    tokens = tokens.trim();
    //    System.out.println(tokens);
        printer.print(tokens);
        printer.close();
    }

    // Method to print error messages and tokens up to error.
    public static void printErrorMessages(String error) {
        tokens = tokens.trim();
        System.out.println(tokens);
        printer.println(tokens);
        System.out.println("LEXICAL ERROR [" + row + ":" + (col + 1) + "]: Invalid token '" + error + "'");
        printer.print("LEXICAL ERROR [" + row + ":" + (col + 1) + "]: Invalid token '" + error + "'");
        printer.close();
        System.exit(1);
    }

    // Method to find out if the given lexeme is keyword or not.
    public static boolean isAKeyword(String line, String keyword) {
        String str;
        // If it is lexeme is true or false it means it is boolean
        if (keyword.equals("true") || keyword.equals("false"))
            str = "boolean";
        else
            str = keyword;

        // If lexeme is equal to given keyword
        if (line.substring(col).length() >= keyword.length() && line.substring(col, col + keyword.length()).equals(keyword)) {
            // If taken lexeme is at the end of the line
            if (line.substring(col).length() == keyword.length()) {
                tokens += str.toUpperCase() + " " + row + ":" + (col + 1) + "\n";
                col = col + keyword.length();
                return true;
            // After the lexeme, if there is space or bracket or comment line
            } else if (line.charAt(col + keyword.length()) == ' ' || line.charAt(col + keyword.length()) == '~' || line.charAt(col + keyword.length()) == '(' || line.charAt(col + keyword.length()) == ')' ||
                    line.charAt(col + keyword.length()) == '[' || line.charAt(col + keyword.length()) == ']' || line.charAt(col + keyword.length()) == '{' || line.charAt(col + keyword.length()) == '}' || col + keyword.length() == line.length() - 1) {
                tokens += str.toUpperCase() + " " + row + ":" + (col + 1) + "\n";
                col = col + keyword.length();
                return true;
            }
        }
        // Otherwise it is not a keyword
        return false;
    }

    // Method to find out if the given current char generates number or not.
    public static void isNumber(String line, char currentCh) {
        String number = "" + currentCh;
        int currentcol = col + 1;
        // Take the lexeme character by character until encounter with the space or bracket or tilde
        while ((line.length() - 1) >= currentcol && (line.charAt(currentcol) != '~' && line.charAt(currentcol) != ' ' && line.charAt(currentcol) != '('
                && line.charAt(currentcol) != ')' && line.charAt(currentcol) != '{' && line.charAt(currentcol) != '}' && line.charAt(col + 1) != '[' && line.charAt(col + 1) != ']')) {
            if ((line.length() - 1) >= currentcol) { // if it is not an end of the line go on
                number += line.charAt(currentcol);
                currentcol++;
            } else {
                break;
            }
        }

        // If lexeme begins with 0b, it can be a binary number
        if (number.length() >= 3 && currentCh == '0' && line.charAt(col + 1) == 'b') {
            boolean isBinary = true;
            int counterCol = col + 2;

            while (true) { // If all numbers after b are 0 or 1, it is a binary number. Otherwise print an error message
                if ((number.length() - 1) >= col && (line.charAt(counterCol) == '0' || line.charAt(counterCol) == '1')) {
                    counterCol++;
                    if (number.length() == counterCol) { // Break the loop when you reach the end of the number
                        break;
                    }
                }
                else {
                    isBinary = false;
                    printErrorMessages(number);
                    break;
                }
            }
            // If it is a binary number, add it to tokens string
            if (isBinary) {
                tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                col += number.length();
            }
        // If lexeme begins with 0x, it can be a hexadecimal number
        } else if (number.length() >= 3 && currentCh == '0' && line.charAt(col + 1) == 'x') {
            boolean isHex = true;
            int counterCol = col + 2;
            while (true) { // If all character after b are numbers or letter between a and f, it is a hexadecimal number
                if ((number.length() - 1) >= col && ((line.charAt(counterCol) <= '9' && line.charAt(counterCol) >= '0') || (line.charAt(counterCol) <= 'F' && line.charAt(counterCol) >= 'A') || (line.charAt(counterCol) <= 'f' && line.charAt(counterCol) >= 'a'))) {
                    counterCol++;
                    if (number.length() == counterCol) { // Break the loop when you reach the end of the number
                        break;
                    }
                }
                else {  // Otherwise print an error message
                    isHex = false;
                    printErrorMessages(number);
                    break;
                }
            }
            // If it is a hexadecimal number, add it to tokens string
            if (isHex) {
                tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                col += number.length();
            }
        }
        else {
            // If it contains point, it can be a floating point number
            if (number.contains(".")) {
                // Check if first character of number is plus, minus or number or not
                if (number.charAt(0) == '+' || number.charAt(0) == '-' || (number.charAt(0) >= '0' && number.charAt(0) <= '9')) {
                    int currentIndex = 1;
                    while (true) { // Until encounter with point increment index for each decimal digit
                        if (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                            currentIndex++;
                        } else if (number.charAt(currentIndex) != '.') {
                            printErrorMessages(number);
                        } else {
                            break;
                        }
                    }
                    currentIndex++;
                    // After point there must be at least 1 decimal digit, check it
                    if ((number.length() - 1) >= currentIndex && number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                        currentIndex++;
                        while (true) { // Until encounter with character other than number, increment current index
                            if ((number.length() - 1) >= currentIndex && number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                                currentIndex++;
                            }
                            else {
                                break;
                            }
                        }
                    // If after the point there is no decimal digit, print an error message and exit the system
                    } else {
                        printErrorMessages(number);
                    }

                    // After decimal digits it can contain 'E' or 'e'
                    if ((number.length() - 1) >= currentIndex && (number.charAt(currentIndex) == 'E' || number.charAt(currentIndex) == 'e')) {
                        currentIndex++;
                        // After 'E' or 'e', it must continue with number, minus or plus
                        if (number.charAt(currentIndex) == '+' || number.charAt(currentIndex) == '-' || (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9')) {
                            currentIndex++;
                            while (number.length() > currentIndex) { // Then, it can continue with decimal digits
                                if ((number.length() - 1) >= currentIndex && number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                                    currentIndex++;
                                } else { // If it does not continue with decimal digits, print an error message
                                    printErrorMessages(number);
                                }
                            }
                            tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                            col += number.length();
                        }
                    // If it does not have 'E' or 'e', it is still a number
                    } else if (number.length() == currentIndex) {
                        tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                        col += number.length();
                    }
                    else {
                        printErrorMessages(number);
                    }
                }

                // Number can start with a point
                if (number.charAt(0) == '.') {
                    int currentIndex = 1;
                    // After point there must be at least 1 decimal digit
                    if (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                        currentIndex++;
                        while (true) {
                            if ((number.length() - 1) >= currentIndex && number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                                currentIndex++;
                            } else {
                                break;
                            }
                        }
                    }
                    // After decimal digits it can contain 'E' or 'e'
                    if ((number.length() - 1) >= currentIndex && (number.charAt(currentIndex) == 'E' || number.charAt(currentIndex) == 'e')) {
                        currentIndex++;
                        // After 'E' or 'e', it must continue with number, minus or plus
                        if (number.charAt(currentIndex) == '+' || number.charAt(currentIndex) == '-' || (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9')) {
                            // If first character after the point is not decimal digit, then second one must be a decimal digit. Otherwise print an error message
                            if (!(number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') && !(number.charAt(currentIndex + 1) >= '0' && number.charAt(currentIndex + 1) <= '9')) {
                                printErrorMessages(number);
                            }
                            while (number.length() > currentIndex) { // Then, it can continue with decimal digits
                                if ((line.length() - 1) >= currentIndex && number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                                    currentIndex++;
                                }
                                else { // If it does not continue with decimal digits, print an error message
                                    printErrorMessages(number);
                                }
                            }
                            tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                            col += number.length();
                        }
                    // If it does not have 'E' or 'e', it is still a number
                    } else if (number.length() == currentIndex) {
                        tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                        col += number.length();
                    } else {
                        printErrorMessages(number);
                    }
                }

            }
            // If number does not contain point but 'E' or 'e'
            else if (number.contains("E") || number.contains("e")) {
                int currentIndex = 0;
                // First character of the number must be plus, minus or decimal digit
                if (number.charAt(currentIndex) == '+' || number.charAt(currentIndex) == '-' || (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9')) {
                    // If first character after the point is not decimal digit, then second one must be a decimal digit. Otherwise print an error message
                    if (!(number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') && !(number.charAt(currentIndex + 1) >= '0' && number.charAt(currentIndex + 1) <= '9')) {
                        printErrorMessages(number);
                    }
                    currentIndex++;

                    while (true) { // Until encounter with 'E' or 'e', increment index for each decimal digit
                        if (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                            currentIndex++;
                        } else if (number.charAt(currentIndex) == 'E' || number.charAt(currentIndex) == 'e') {
                            currentIndex++;
                            break;
                        } else {
                            printErrorMessages(number);
                        }
                    }

                    currentIndex++;

                    // After 'E' or 'e', it must continue with number, minus or plus
                    if (number.length() > 2 && (number.charAt(currentIndex) == '+' || number.charAt(currentIndex) == '-' || (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9'))) {
                        // If first character after 'e'/'E' is not decimal digit, then second one must be a decimal digit. Otherwise print an error message
                        if (!(number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') && !(number.charAt(currentIndex + 1) >= '0' && number.charAt(currentIndex + 1) <= '9')) {
                            printErrorMessages(number);
                        }
                        while (number.length() > currentIndex) {
                            if (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                                currentIndex++;
                            } else {
                                printErrorMessages(number);
                            }
                        }
                        tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                        col += number.length();
                    }
                    else if (number.length() == 2 && !number.contains("e") && !number.contains("E") && (number.charAt(1) >= '0' && number.charAt(1) <= '9')) {
                        tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                        col += number.length();
                    } else {
                        printErrorMessages(number);
                    }
                }

            }
            // If number does not contain point or 'e' or 'E, it can be decimal signed integer, first character of it must be plus, minus or decimal digit
            else if ((number.charAt(0) == '+' || number.charAt(0) == '-' || (number.charAt(0) >= '0' && number.charAt(0) <= '9')) && isCorrectNumber(number)) {
                int currentIndex = 1;
                // If first character after the point is not decimal digit, then second one must be a decimal digit. Otherwise print an error message
                if (number.length() > currentIndex && !(number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') && !(number.charAt(currentIndex + 1) >= '0' && number.charAt(currentIndex + 1) <= '9')) {
                    printErrorMessages(number);
                }
                while (number.length() > currentIndex) { // Following the first character, all digits must be decimal digit
                    if (number.charAt(currentIndex) >= '0' && number.charAt(currentIndex) <= '9') {
                        if (number.length() != currentIndex + 1)
                            currentIndex++;
                        else
                            break;
                    }
                    else {
                        printErrorMessages(number);
                    }
                }
                tokens += "NUMBER " + row + ":" + (col + 1) + "\n";
                col += number.length();
            } else {
                printErrorMessages(number);
            }
        }
    }

    // Method to figure out if given number contains character other than decimal digit, plus, minus or point
    public static boolean isCorrectNumber(String str) {
        int i = 0;
        while (i < str.length()) {
            if ((str.charAt(i) <= '9' && str.charAt(i) >= '0') || str.charAt(i) == '+' || str.charAt(i) == '.' || str.charAt(i) == '-')
                i++;
            else
                return false;
        }
        return true;
    }
}