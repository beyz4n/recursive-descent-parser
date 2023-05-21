import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SyntaxAnalyser {
    static String line;
    static String output = "";
    static Scanner scanner1;
    static PrintWriter printWriter;
    static boolean fileFinished = false;
    static long emptySpaceCounter = 0;
    static File file;
    static ArrayList<String> lexemes;


    public static void main(String[] args) throws Exception {

        System.out.print("Enter the name of the input file: ");
        Scanner scanner = new Scanner(System.in);
        String inputFileName = scanner.nextLine();
        file = new File(inputFileName);

        while (!file.exists()) {
            System.out.print("You entered a file which does not exist. Please enter a valid one: ");
            scanner = new Scanner(System.in);
            inputFileName = scanner.nextLine();
            file = new File(inputFileName);
        }

        File outputFile = new File("output.txt");
        printWriter = new PrintWriter(outputFile);

        new LexicalAnalyser(file);
        Scanner scanner2 = new Scanner(file);
        lexemes = new ArrayList<>();
        while(scanner2.hasNextLine()){
            lexemes.add(scanner2.nextLine());
        }

        File lex = new File("lexical_output.txt");
        scanner1 = new Scanner(lex);
        nextLine();
        Program();
        printWriter.print(output.trim());
        System.out.print(output.trim());
        printWriter.close();
    }

    public static void nextLine(){
        if(scanner1.hasNextLine()){
            line = scanner1.nextLine();
        }
        else{
            fileFinished = true;
        }
    }

    public static String emptySpacePrinter() {
        String str = "";
        for (long i = 0; i < emptySpaceCounter; i++) {
            str += " ";
        }
        return str;
    }

    // The function to print error
    public static void printError(String error) {
        // Add error text to output string
        output += "SYNTAX ERROR [" + line.split(" ")[1] + "]: '" + error + "' is expected";
        printWriter.print(output); // Print output to output file
        printWriter.close();
        System.out.println(output); // Print output to console
        System.exit(1); // Exit the program
    }

    // The function to get lexemes from the input txt
    public static String getActualLexeme(){
        String str = "";
        String type = line.split(" ")[0]; // Take token type of the lexeme from the output file of the Lexical Analyser
        String point = line.split(" ")[1]; // Take index of the lexeme from the output file of the Lexical Analyser
        int row = Integer.parseInt(point.split(":")[0]); // row index of the lexeme
        int col = Integer.parseInt(point.split(":")[1]); // column index of the lexeme
        str = lexemes.get(row - 1); // get the line that contained desired lexeme
        str = str.substring(col - 1);

        // if the token is not string and not character
        if(!type.startsWith("STRING") && !type.startsWith("CHAR")) {
            String lexeme = "";
            int count = 0;
            while (count < str.length() && !(str.charAt(count) == ' ' || str.charAt(count) == '(' || str.charAt(count) == ')' ||
                    str.charAt(count) == '[' || str.charAt(count) == ']' || str.charAt(count) == '{' || str.charAt(count) == '}' || str.charAt(count) == '~')) {
                lexeme += str.charAt(count); // take lexeme character by character
                count++;
            }
            return lexeme; // return the lexeme
        }
        // else, if token is string
        else if(type.startsWith("STRING")){
            // Take first character of the lexeme which is (")
            String lexeme = "" + str.charAt(0);
            int count = 1;

            while(count < str.length()){
                lexeme += str.charAt(count); // take lexeme character by character
                // If character is " and previous one is not \, break since the string is completed.
                if(str.charAt(count) == '"' && str.charAt(count-1) != '\\')
                    break;
                count++;
            }

            return lexeme; // return the lexeme
        }
        // else, if token is character
        else {
            // Take first character of the lexeme which is (')
            String lexeme = "" + str.charAt(0);
            int count = 1;

            while(count < str.length()){
                lexeme += str.charAt(count); // take lexeme character by character
                // If character is ' and previous one is not \, break since the string is completed.
                if(str.charAt(count) == '\'' && str.charAt(count-1) != '\\')
                    break;
                count++;
            }

            return lexeme; // return the lexeme
        }
    }

    // Function to get bracket according to its type
    public static String getTheBracket(String bracketType, String pos){
        // if type is PAR and position is right, return right parenthesis
        if(bracketType.equals("PAR") && pos.equals("r"))
            return ")";
        // if type is PAR and position is left, return left parenthesis
        else if(bracketType.equals("PAR") && pos.equals("l"))
            return "(";
        // if type is SQUAREB and position is right, return right square bracket
        else if(bracketType.equals("SQUAREB") && pos.equals("r"))
            return "]";
        // if type is SQUARE and position is left, return left square bracket
        else if(bracketType.equals("SQUAREB") && pos.equals("l"))
            return "[";
        // if type is CURLYB and position is right, return right curly bracket
        else if(bracketType.equals("CURLYB") && pos.equals("r"))
            return "}";
        // if type is CURLYB and position is left, return left curly bracket
        else
            return "{";
    }


    // This method applies the grammar of Program
    public static void Program() {
        // adds the program to the output using the empty space counter
        output += emptySpacePrinter() + "<Program>" + "\n";
        // increase the empty space counter since we entered a rule
        emptySpaceCounter++;
        // check if file is finished or we have a left parenthesis
        if (!fileFinished && (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB"))) {
            //call these functions according to the  grammar
            TopLevelForm();
            Program();
        }
        // if it is epsilon then add it to output
        else {
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
        // when we are done with the function decrease the counter
        emptySpaceCounter--;
    }
    // This method applies the grammar of TopLevelForm
    public static void TopLevelForm() {
        // add the function to the output and increment the counter
        output += emptySpacePrinter() + "<TopLevelForm>" + "\n";
        emptySpaceCounter++;
        // check if we start with a left parenthesis
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            // keep the bracket type
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            // add the parenthesis to the output
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            // call the functions according to the grammar
            nextLine();
            SecondLevelForm();
            // when the calls are done check if we have the right and same type of parenthesis
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    // if we dont have right parenthesis print error
                    printError(getTheBracket(bracketType, "r"));
                }
                else{
                    // if we have add to output
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine();
                }
            }
            // if we dont have correct bracket type print error
            else {
                printError(getTheBracket(bracketType, "r"));
            }
        }
        // if we didnt have left parenthesis at beginning give error
        else {
            printError("(");
        }
        // after function is done decrement the counter
        emptySpaceCounter--;
    }

    // This method applies the grammar of Second Level Form
    public static void SecondLevelForm() {
        // add it to the output and increment the counter
        output += emptySpacePrinter() + "<SecondLevelForm>" + "\n";
        emptySpaceCounter++;
        // check if we start with a left parenthesis
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            // keep the bracket type
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            // add the parenthesis to the output
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            // call other functions according to the grammar
            nextLine();
            FunCall();
            // when the calls are done check if we have the right and same type of parenthesis
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    // if we dont have right parenthesis print error
                    printError(getTheBracket(bracketType, "r"));
                }
                else{
                    // if we have then add to output
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine();
                }
            }
            else {
                // if we dont have right bracket type print error
                printError(getTheBracket(bracketType, "r"));
            }
        }
        // or call definition if there is no left parenthesis
        else {
            Definition();
        }
        // decrement the counter since we are at the end of the function
        emptySpaceCounter--;
    }

    // This function applies the grammar of the definiton
    public static void Definition() {
        // puts the function to the output and increment the counter
        output += emptySpacePrinter() + "<Definition>" + "\n";
        emptySpaceCounter++;
        // if we have define
        if (line.startsWith("DEFINE")) {
            // add it to the output
            output += emptySpacePrinter() + "DEFINE (" + getActualLexeme() + ")\n";
            // call necessary functions
            nextLine();
            DefinitionRight();
        }
        // if there is no define give error
        else {
            printError("DEFINE");
        }
        // decrement the counter
        emptySpaceCounter--;
    }

    // This function applies the grammar of Definition Right
    public static void DefinitionRight() {
        // add the function to the output and increment the counter
        output += emptySpacePrinter() + "<DefinitionRight>" + "\n";
        emptySpaceCounter++;
        // if we have identifier then
        if (line.startsWith("IDENTIFIER")) {
            // put it to the output and call expression
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() +  ")\n";
            Expression();
        }
        // if we have a left parenthesis
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            // keep the bracket type
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            // put left parenthesis to output
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine();
            // check if it is identifier
            if (line.startsWith("IDENTIFIER")) {
                // put it to the output call new functions
                output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() +  ")\n";
                nextLine();
                ArgList();
                // if we have right and correct type of parenthesis
                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        // put it to the output and call the new functions
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                        nextLine();
                        Statements();
                    }
                    // if we dont have right parenthesis print error
                    else {
                        printError(getTheBracket(bracketType, "r"));
                    }
                }
                // if we dont have the right bracket type print error
                else {
                    printError(getTheBracket(bracketType, "r"));
                }
            }
            // we expect an identifier, print error
            else {
                printError("IDENTIFIER");
            }
        }
        // we expect left par or identifier print error
        else {
            printError("(/IDENTIFIER");
        }
        // end of function decrement the counter
        emptySpaceCounter--;
    }

    // This function applies the grammar of FunCall
    public static void FunCall() {
        // put the function to the output, increment the counter
        output += emptySpacePrinter() + "<FunCall>" + "\n";
        emptySpaceCounter++;
        // if we have identifier put it to the output and call the functions
        if (line.startsWith("IDENTIFIER")) {
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() +  ")\n";
            nextLine();
            Expressions();
        }
        // we expect a identifier, print error
        else {
            printError("IDENTIFIER");
        }
        // decrement the counter since end of function
        emptySpaceCounter--;
    }

    // This function applies the grammar of Expressions
    public static void Expressions() {
        // put the function to the output and increment the counter
        output += emptySpacePrinter() + "<Expressions>" + "\n";
        emptySpaceCounter++;
        // if we have id, number, char, boolean or string call functions
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
            Expressions();
        }
        // if it is epsilon then put it to the output
        else{
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
        // decrement the counter
        emptySpaceCounter--;
    }

    // This method applies the grammar of Expr
    public static void Expr() {
        // put it to the output and increment the counter
        output += emptySpacePrinter() + "<Expr>" + "\n";
        emptySpaceCounter++;
        // according to the keyword call the expression for them
        if (line.startsWith("LET")) {
            LetExpression();
        }
        else if (line.startsWith("COND")) {
            CondExpression();
        }
        else if (line.startsWith("IF")) {
            IfExpression();
        }
        else if (line.startsWith("BEGIN")) {
            BeginExpression();
        }
        // for identifier, we call funCall
        else if (line.startsWith("IDENTIFIER")) {
            FunCall();
        }
        // we expect these print error
        else {
            printError("LET/COND/IF/BEGIN/IDENTIFIER");
        }
        // decrement counter
        emptySpaceCounter--;
    }

    // This method applies the grammar of Expression
    public static void Expression() {
        // put it to the output and increment the counter
        output += emptySpacePrinter() + "<Expression>" + "\n";
        emptySpaceCounter++;
        // check if we have one of these, if we have put it to the output and call the next line
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING")) {
            output += emptySpacePrinter() + line.split(" ")[0] + " (" + getActualLexeme() + ")\n";
            nextLine();
        }
        // if we have left parenthesis
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            // keep the bracket type
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            // put the left parenthesis to the output and call necessary rules
            output += emptySpacePrinter() + "LEFT" + bracketType +"(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine();
            Expr();
            // if we have right and correct brackets
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    // if we dont have print error
                    printError(getTheBracket(bracketType, "r"));
                }
                else{
                    // put it to output and call the next line
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine();
                }
            }
            // if we dont have print error
            else{
                printError(getTheBracket(bracketType, "r"));
            }
        }
        // if none, then print error with expecting these
        else {
            printError("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(");
        }
        // decrement the counter
        emptySpaceCounter--;
    }

    // This function applies the grammar of LetExpression
    public static void LetExpression() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<LetExpression>" + "\n";
        emptySpaceCounter++; // increment space counter
        // if we have let
        if (line.startsWith("LET")) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LET (" + getActualLexeme() + ")\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            LetExpr();
        }
        else { // if we don't have LET, print error
            printError("LET");
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of LetExpr
    public static void LetExpr() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<LetExpr>" + "\n";
        emptySpaceCounter++;  // increment space counter

        // if we have identifier
        if (line.startsWith("IDENTIFIER")) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
            nextLine(); // take next line from the output pf the Lexical Analyser

            // if we have left parenthesis
            if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
                String bracketType = line.split(" ")[0];
                bracketType = bracketType.substring(4); // Keep bracket type
                // add it to output string with its space needed for alignment
                output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
                nextLine(); // take next line from the output pf the Lexical Analyser
                VarDefs();

                // if the bracket types match
                if (line.contains(bracketType)) {
                    // if we have right parenthesis
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        // add it to output string with its space needed for alignment
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                        nextLine(); // take next line from the output pf the Lexical Analyser
                        Statements();
                    }
                    else { // if we don't have right parenthesis, print error
                        printError(getTheBracket(bracketType, "r"));
                    }
                }
                else { // if parenthesis types does not match, print error
                    printError(getTheBracket(bracketType, "r"));
                }
            }
            else {  // if we don't have left parenthesis, print error
                printError("(");
            }
        } // if we have left parenthesis but not identifier
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4); // Keep bracket type
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            VarDefs();

            // if the bracket types match
            if (line.contains(bracketType)) {
                // if we have right parenthesis
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    // add it to output string with its space needed for alignment
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine(); // take next line from the output pf the Lexical Analyser
                    Statements();
                }
                else { // if we don't have right parenthesis, print error
                    printError(getTheBracket(bracketType, "r"));
                }
            }
            else { // if parenthesis types does not match, print error
                printError(getTheBracket(bracketType, "r"));
            }
        }
        else { // if we don't have identifier or left parenthesis, print error
            printError("IDENTIFIER or (");
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of CondExpression
    public static void CondExpression() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<CondExpression>" + "\n";
        emptySpaceCounter++; // increment space counter

        // if we have cond
        if (line.startsWith("COND")) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "COND (" + getActualLexeme() + ")\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            CondBranches();
        }
        else { // if we don't have cond, print error
            printError("COND");
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of CondBranches
    public static void CondBranches() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<CondBranches>" + "\n";
        emptySpaceCounter++; // increment space counter

        // if we have left parenthesis
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4); // Keep bracket type
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            Expression();
            Statements();

            // if the bracket types match
            if (line.contains(bracketType)) {
                // if we have right parenthesis
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    // add it to output string with its space needed for alignment
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine(); // take next line from the output pf the Lexical Analyser
                    CondBranch();
                }
                else { // if we don't have right parenthesis, print error
                    printError(getTheBracket(bracketType, "r"));
                }
            }
            else { // if parenthesis types does not match, print error
                printError(getTheBracket(bracketType, "r"));
            }
        } // if we don't have left parenthesis, print error
        else {
            printError("(");
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of CondBranch
    public static void CondBranch() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<CondBranch>" + "\n";
        emptySpaceCounter++; // increment space counter

        // if we have left parenthesis
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4); // Keep bracket type
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            Expression();
            Statements();

            // if the bracket types match
            if (line.contains(bracketType)) {
                // if we don't have right parenthesis, print error
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(getTheBracket(bracketType, "r"));
                }
                else { // if we have right parenthesis, print error
                    // add it to output string with its space needed for alignment
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                }
            }
            else { // if parenthesis types does not match, print error
                printError(getTheBracket(bracketType, "r"));
            }
        }
        else{ // if we don't have left parenthesis, print error
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "__" + '\n';
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of IfExpression
    public static void IfExpression() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<IfExpression>" + "\n";
        emptySpaceCounter++; // increment space counter
        // if we have if
        if (line.startsWith("IF")) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "IF (" + getActualLexeme() + ")\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            Expression();
            Expression();
            EndExpression();
        }
        else { // if we don't have if, print error
            printError("IF");
        }
        emptySpaceCounter--; // decrement space counter
    }

    public static void EndExpression() {
        output += emptySpacePrinter() + "<EndExpresssion>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void BeginExpression() {
        output += emptySpacePrinter() + "<BeginExpression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("BEGIN")) {
            nextLine();
            output += emptySpacePrinter() + "BEGIN (" + getActualLexeme() + ")\n";
            Statements();
        } else {
            printError("BEGIN");
        }
        emptySpaceCounter--;
    }

    public static void VarDefs() {
        output += emptySpacePrinter() + "<VarDefs>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            if (line.startsWith("IDENTIFIER")) {
                output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
                nextLine();
                Expression();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                        VarDef();
                    }
                    else {
                        printError(getTheBracket(bracketType, "r"));
                    }
                }
                else {
                    printError(getTheBracket(bracketType, "r"));
                }
            }
            else {
                printError("IDENTIFIER");
            }
        }
        emptySpaceCounter--;
    }

    public static void VarDef() {
        output += emptySpacePrinter() + "<VarDef>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            VarDefs();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void ArgList() {
        output += emptySpacePrinter() + "<ArgList>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER")) {
            nextLine();
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
            ArgList();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void Statements() {
        output += emptySpacePrinter() + "<Statements>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("DEFINE")) {
            nextLine();
            output += emptySpacePrinter() + "DEFINE (" + getActualLexeme() + ")\n";
            Definition();
            Statements();
        } else {
            Expression();
        }
        emptySpaceCounter--;
    }
}

