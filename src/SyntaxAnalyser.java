import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SyntaxAnalyser {
    static String line; // this is the string where we store the current line of the input file
    static String output = ""; // this is the string where we store the output of the lexical analyser
    static Scanner scanner1;
    static PrintWriter printWriter;
    static boolean fileFinished = false; // boolean variable to mark the end of a file
    static long emptySpaceCounter = 0; // used to count the number of space chars needed
    static File file;
    static ArrayList<String> lexemes; // list containing all the lexemes

    // this is our main method where we integrate everything
    public static void main(String[] args) throws Exception {
        // prompting the user to enter the input file path
        System.out.print("Enter the name of the input file: ");
        Scanner scanner = new Scanner(System.in);
        String inputFileName = scanner.nextLine();
        file = new File(inputFileName); // creating a file object for the input file
        // here we check if the input file exists, if not we prompt the user to enter an input file path again
        while (!file.exists()) {
            System.out.print("You entered a file which does not exist. Please enter a valid one: ");
            scanner = new Scanner(System.in);
            inputFileName = scanner.nextLine();
            file = new File(inputFileName);
        }
        // creating output file to print the results of the syntax analyser
        File outputFile = new File("output.txt");
        printWriter = new PrintWriter(outputFile);
        // creating an object of the lexical analyser to convert the input into tokens which our syntax analyser can analyse
        new LexicalAnalyser(file);
        Scanner scanner2 = new Scanner(file);
        lexemes = new ArrayList<>();
        // adding the lexemes to an arraylist to access them, for the gerActualLexeme method
        while(scanner2.hasNextLine()){
            lexemes.add(scanner2.nextLine());
        }
        // creating an output file to read the output of the lexical analyser
        File lex = new File("lexical_output.txt");
        scanner1 = new Scanner(lex);
        // starting the recursive descent parser algorithm with the program method
        nextLine();
        Program();
        // printing the output to the output.txt file and to the console
        printWriter.print(output.trim());
        System.out.print(output.trim());
        printWriter.close();
    }
    // this method is to simplify the setting of the next line
    public static void nextLine(){
        // checking if a next line exist
        if(scanner1.hasNextLine()){
            line = scanner1.nextLine(); // if it exists, setting the line accordingly
        }
        else{
            fileFinished = true; // if it doesn't exist, setting the fileFinished boolean variable accordingly
        }
    }
    // this function is there to align the output accordingly to the given  documentation
    public static String emptySpacePrinter() {
        // creating an empty string to store the space characters
        String str = "";
        // setting the according number of space characters
        for (long i = 0; i < emptySpaceCounter; i++) {
            str += " ";
        }
        // returning the string consisting with the requested number of space characters
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
    // this method is used to check if a file finished and prints an according error if it did
    public static void printErrorDueToFileFinished(String error) {
        if(fileFinished) {
            String str = line.split(" ")[1];
            int row = Integer.parseInt(str.split(":")[0]); // find row index
            int col = Integer.parseInt(str.split(":")[1]); // find column index
            col += (getActualLexeme().length() + 1); // add the length to the column index
            // Add error text to output string
            output += "SYNTAX ERROR [" + row + ":" + col + "]: '" + error + "' is expected";
            printWriter.print(output); // Print output to output file
            printWriter.close();
            System.out.println(output); // Print output to console
            System.exit(1); // Exit the program
        }
    }

    // The function to get lexemes from the input txt
    public static String getActualLexeme(){
        if(line.startsWith("LEFTPAR"))
            return "(";
        if(line.startsWith("RIGHTPAR"))
            return ")";

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
            while (count < str.length() && !(str.charAt(count) == ' ' || str.charAt(count) == '(' || str.charAt(count) == ')' || str.charAt(count) == '~')) {
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

    // This method applies the grammar of Program
    public static void Program() {
        // adds the program to the output using the empty space counter
        output += emptySpacePrinter() + "<Program>" + "\n";
        // increase the empty space counter since we entered a rule
        emptySpaceCounter++;
        // check if file is finished or we have a left parenthesis
        if (!fileFinished && (line.startsWith("LEFTPAR"))) {
            //call these functions according to the  grammar
            TopLevelForm();
            Program();
        }
        // if it is epsilon then add it to output
        else if(fileFinished){
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
        else {
            printError("(");
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
        if (line.startsWith("LEFTPAR")) {
            // add the parenthesis to the output
            output += emptySpacePrinter() + "LEFTPAR " + "(()" + "\n";
            // call the functions according to the grammar
            nextLine();
            printErrorDueToFileFinished("(/DEFINE");
            SecondLevelForm();
            // when the calls are done check if we have the right and same type of parenthesis
            if (!line.startsWith("RIGHTPAR")) {
                // if we don't have right parenthesis print error
                printError(")");
            }
            else{
                // if we have, add to output
                output +=  emptySpacePrinter() + "RIGHTPAR " + "())" + "\n";
                nextLine();
                printErrorDueToFileFinished("(/EPSILON");
            }
        }
        // if we didn't have left parenthesis at beginning give error
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
        if (line.startsWith("LEFTPAR")) {
            // add the parenthesis to the output
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            // call other functions according to the grammar
            nextLine();
            printErrorDueToFileFinished("IDENTIFIER");
            FunCall();
            // when the calls are done check if we have the right parenthesis
            if (!line.startsWith("RIGHTPAR")) {
                // if we don't have right parenthesis print error
                printError(")");
            }
            else{
                // if we have then, add to output
                output +=  emptySpacePrinter() + "RIGHTPAR " + "())" + "\n";
                nextLine();
                printErrorDueToFileFinished(")");
            }
        }
        // or call definition if there is no left parenthesis
        else {
            Definition();
        }
        // decrement the counter since we are at the end of the function
        emptySpaceCounter--;
    }

    // This function applies the grammar of the definition
    public static void Definition() {
        // puts the function to the output and increment the counter
        output += emptySpacePrinter() + "<Definition>" + "\n";
        emptySpaceCounter++;
        // if we have "define"
        if (line.startsWith("DEFINE")) {
            // add it to the output
            output += emptySpacePrinter() + "DEFINE (" + getActualLexeme() + ")\n";
            // call necessary functions
            nextLine();
            printErrorDueToFileFinished("(/IDENTIFIER");
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
        else if (line.startsWith("LEFTPAR")) {
            // put left parenthesis to output
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            nextLine();
            printErrorDueToFileFinished("IDENTIFIER");
            // check if it is identifier
            if (line.startsWith("IDENTIFIER")) {
                // put it to the output call new functions
                output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() +  ")\n";
                nextLine();
                printErrorDueToFileFinished("IDENTIFIER/EPSILON");
                ArgList();
                // if we have right and correct type of parenthesis
                if (line.startsWith("RIGHTPAR")  ) {
                    // put it to the output and call the new functions
                    output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                    nextLine();
                    printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/DEFINE");
                    Statements();
                }
                // if we don't have right parenthesis print error
                else {
                    printError(")");
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
            printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/EPSILON");
            Expressions();
        }
        // we expect an identifier, print error
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
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR")  ) {
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
            printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/EPSILON");
        }
        // if we have left parenthesis
        else if (line.startsWith("LEFTPAR")  ) {
            // put the left parenthesis to the output and call necessary rules
            output += emptySpacePrinter() + "LEFTPAR" +"(()" + "\n";
            nextLine();
            printErrorDueToFileFinished("LET/COND/IF/BEGIN/IDENTIFIER");
            Expr();
            // if we have right and correct brackets
                if (!line.startsWith("RIGHTPAR")) {
                    // if we don't have print error
                    printError(")");
                }
                else{
                    // put it to output and call the next line
                    output +=  emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                    nextLine();
                    printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/EPSILON/)");
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
            printErrorDueToFileFinished("IDENTIFIER");
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
            printErrorDueToFileFinished("(");

            // if we have left parenthesis
            if (line.startsWith("LEFTPAR")  ) {
                // add it to output string with its space needed for alignment
                output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
                nextLine(); // take next line from the output pf the Lexical Analyser
                printErrorDueToFileFinished("EPSILON/(");
                VarDefs();
                // if we have right parenthesis
                if (line.startsWith("RIGHTPAR")  ) {
                    // add it to output string with its space needed for alignment
                    output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                    nextLine(); // take next line from the output pf the Lexical Analyser
                    printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/DEFINE");
                    Statements();
                }
                else { // if we don't have right parenthesis, print error
                    printError(")");
                }
            }
            else {  // if we don't have left parenthesis, print error
                printError("(");
            }
        } // if we have left parenthesis but not identifier
        else if (line.startsWith("LEFTPAR")  ) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            printErrorDueToFileFinished("(/EPSILON");
            VarDefs();
            // if we have right parenthesis
            if (line.startsWith("RIGHTPAR")  ) {
                // add it to output string with its space needed for alignment
                output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                nextLine(); // take next line from the output pf the Lexical Analyser
                printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(/DEFINE");
                Statements();
            }
            else { // if we don't have right parenthesis, print error
                printError(")");
            }
        }
        else { // if we don't have identifier or left parenthesis, print error
            printError("IDENTIFIER/(");
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
            printErrorDueToFileFinished("(");
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
        if (line.startsWith("LEFTPAR")  ) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(");
            Expression();
            Statements();
            // if we have right parenthesis
            if (line.startsWith("RIGHTPAR")  ) {
                // add it to output string with its space needed for alignment
                output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                nextLine(); // take next line from the output pf the Lexical Analyser
                printErrorDueToFileFinished("EPSILON/(");
                CondBranch();
            }
            else { // if we don't have right parenthesis, print error
                printError(")");
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
        if (line.startsWith("LEFTPAR")  ) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(");
            Expression();
            Statements();

            // if we don't have right parenthesis, print error
            if (!line.startsWith("RIGHTPAR")) {
                printError(")");
            }
            else { // if we have right parenthesis, print error
                // add it to output string with its space needed for alignment
                output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                nextLine();
                printErrorDueToFileFinished(")");
            }

        }
        else{ // if we don't have left parenthesis
            // add ε to output string with its space needed for alignment
            output += emptySpacePrinter() + "__" + '\n';
        }
        emptySpaceCounter--; // decrement space counter
    }

    // This function applies the grammar of IfExpression
    public static void IfExpression() {
        // add it to output string with its space needed for alignment
        output += emptySpacePrinter() + "<IfExpression>" + "\n";
        emptySpaceCounter++; // increment space counter

        // if we have "if"
        if (line.startsWith("IF")) {
            // add it to output string with its space needed for alignment
            output += emptySpacePrinter() + "IF (" + getActualLexeme() + ")\n";
            nextLine(); // take next line from the output pf the Lexical Analyser
            printErrorDueToFileFinished("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(");
            Expression();
            Expression();
            EndExpression();
        }
        else { // if we don't have if, print error
            printError("IF");
        }
        emptySpaceCounter--; // decrement space counter
    }
    // this method is used to apply the grammar of EndExpression
    public static void EndExpression() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<EndExpression>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes
        // checking which method to call
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR")  ) {
            // calling the Expression method if the next line contains one of the tokens from above
            Expression();
        }
        else{
            // if not adding the string for the epsilon option
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--; // decreasing the space counter for alignment purposes
    }
    // decreasing the space counter for alignment purposes
    public static void BeginExpression() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<BeginExpression>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes
        // checking if the current token contains a BEGIN token
        if (line.startsWith("BEGIN")) {
            // adding the BEGIN lexeme with the correct alignment to the output string
            output += emptySpacePrinter() + "BEGIN (" + getActualLexeme() + ")\n";
            nextLine();
            // calling the Statements method
            Statements();
        }
        else {
            // if not, calling the printError function with the according parameter
            printError("BEGIN");
        }
        emptySpaceCounter--; // decreasing the space counter for alignment purposes
    }
    // this method is used to apply the grammar of VarDefs
    public static void VarDefs() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<VarDefs>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes
        // checking if the current token contains a type of left parenthesis
        if (line.startsWith("LEFTPAR")) {
            nextLine();
            // adding the correct parenthesis lexeme with the correct alignment to the output string
            output += emptySpacePrinter() + "LEFTPAR" + "(()" + "\n";
            // checking if the current token contains a IDENTIFIER token
            if (line.startsWith("IDENTIFIER")) {
                // if so adding the IDENTIFIER lexeme with the correct alignment
                output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
                nextLine();
                // calling the Expressions method
                Expression();
                // checking if the parenthesis types match
                    // checking if it's right
                    if (line.startsWith("RIGHTPAR")) {
                        nextLine();
                        // adding the correct parenthesis lexeme with the correct alignment
                        output += emptySpacePrinter() + "RIGHTPAR" + "())" + "\n";
                        VarDef();
                    }
                    else {
                        // calling the error method with the missing token
                        printError(")");
                    }
            }
            else {
                // calling the error method with the missing token
                printError("IDENTIFIER");
            }
        }
        else{
            printError("(");
        }
        emptySpaceCounter--; // decreasing the space counter for alignment purposes
    }
    // this method is used to apply the grammar of VarDef
    public static void VarDef() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<VarDef>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes
        // checking which method to call
        if (line.startsWith("LEFTPAR")  ) {
            // calling VarDefs method if the current line contains parenthesis
            VarDefs();
        }
        else{
            // if not adding the string for the epsilon option
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--; // decreasing the space counter for alignment purposes
    }
    // this method is used to apply the grammar of ArgList
    public static void ArgList() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<ArgList>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes
        // checking which method to call
        if (line.startsWith("IDENTIFIER")) {
            // adding the IDENTIFIER lexeme with the correct alignment
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
            nextLine();
            // calling ArgList method
            ArgList();
        }
        else{
            // if not adding the string for the epsilon option
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--; // decreasing the space counter for alignment purposes
    }
    // this method is used to apply the grammar of EndExpression
    public static void Statements() {
        // adding the method name with the correct alignment
        output += emptySpacePrinter() + "<Statements>" + "\n";
        emptySpaceCounter++; // increasing the space counter for alignment purposes

        // checking if the current token contains a DEFINE token
        if (line.startsWith("DEFINE")) {
            nextLine();
            // if so, adding the DEFINE lexeme with the correct alignment
            output += emptySpacePrinter() + "DEFINE (" + getActualLexeme() + ")\n";
            // calling Definition method
            Definition();
            // calling Statements method
            Statements();
        } else {
            // if not, calling Expression method
            Expression();
        }
        emptySpaceCounter--;// decreasing the space counter for alignment purposes
    }
}

