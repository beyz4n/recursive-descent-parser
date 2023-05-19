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
        printWriter.print(output);
        System.out.print(output);
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

    public static void printError(String error) {
        output += "SYNTAX ERROR" + line.split(" ")[1] + "'" + error + "' is expected";
        printWriter.print(output);
        printWriter.close();
        System.out.println(output);
        System.exit(1);
    }
    public static String getActualLexeme(){
        String str = "";
        String type = line.split(" ")[0];
        String point = line.split(" ")[1];
        int row = Integer.parseInt(point.split(":")[0]);
        int col = Integer.parseInt(point.split(":")[1]);
        str = lexemes.get(row - 1);
        str = str.substring(col - 1);
        if(!type.startsWith("STRING") && !type.startsWith("CHAR")) {
            String identifier = "";
            int count = 0;
            while (count < str.length() && !(str.charAt(count) == ' ' || str.charAt(count) == '(' || str.charAt(count) == ')' ||
                    str.charAt(count) == '[' || str.charAt(count) == ']' || str.charAt(count) == '{' || str.charAt(count) == '}' || str.charAt(count) == '~')) {
                identifier += str.charAt(count);
                count++;
            }
            return identifier;
        }
        else if(type.startsWith("STRING")){
            String identifier = "" + str.charAt(0);
            int count = 1;
            while(count < str.length()){
                identifier += str.charAt(count);
                if(str.charAt(count) == '"' && str.charAt(count-1) != '\\')
                    break;
                count++;
            }
            return identifier;
        }
        else {
            String identifier = "" + str.charAt(0);
            int count = 1;
            while(count < str.length()){
                identifier += str.charAt(count);
                if(str.charAt(count) == '\'' && str.charAt(count-1) != '\\')
                    break;
                count++;
            }
            return identifier;
        }
    }

    public static String getTheBracket(String bracketType, String pos){
        if(bracketType.equals("PAR") && pos.equals("r"))
            return ")";
        else if(bracketType.equals("PAR") && pos.equals("l"))
            return "(";
        else if(bracketType.equals("SQUAREB") && pos.equals("r"))
            return "]";
        else if(bracketType.equals("SQUAREB") && pos.equals("l"))
            return "[";
        else if(bracketType.equals("CURLYB") && pos.equals("r"))
            return "}";
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

    public static void LetExpression() {
        output += emptySpacePrinter() + "<LetExpression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LET")) {
            output += emptySpacePrinter() + "LET (" + getActualLexeme() + ")\n";
            nextLine();
            LetExpr();
        } else {
            printError("LET");
        }
        emptySpaceCounter--;
    }

    public static void LetExpr() {
        output += emptySpacePrinter() + "<LetExpr>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER")) {
            output += emptySpacePrinter() + "IDENTIFIER (" + getActualLexeme() + ")\n";
            nextLine();

            if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
                String bracketType = line.split(" ")[0];
                bracketType = bracketType.substring(4);
                output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
                nextLine();
                VarDefs();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                        nextLine();
                        Statements();
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
                printError("(");
            }
        } else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine();
            VarDefs();

            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine();
                    Statements();
                } else {
                    printError(getTheBracket(bracketType, "r"));
                }
            } else {
                printError(getTheBracket(bracketType, "r"));
            }
        }
        else {
            printError("LET or (");
        }
        emptySpaceCounter--;
    }

    public static void CondExpression() {
        output += emptySpacePrinter() + "<CondExpression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("COND")) {
            output += emptySpacePrinter() + "COND (" + getActualLexeme() + ")\n";
            nextLine();
            CondBranches();
        }
        else {
            printError("COND");
        }
        emptySpaceCounter--;
    }

    public static void CondBranches() {
        output += emptySpacePrinter() + "<CondBranches>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine();
            Expression();
            Statements();
            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                    nextLine();
                    CondBranch();
                } else {
                    printError(getTheBracket(bracketType, "r"));
                }
            } else {
                printError(getTheBracket(bracketType, "r"));
            }
        } else {
            printError("(");
        }
        emptySpaceCounter--;
    }

    public static void CondBranch() {
        output += emptySpacePrinter() + "<CondBranch>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(" + getTheBracket(bracketType, "l") + ")" + "\n";
            nextLine();
            Expression();
            Statements();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(getTheBracket(bracketType, "r"));
                }
                else {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(" + getTheBracket(bracketType, "r") + ")" + "\n";
                }
            } else {
                printError(getTheBracket(bracketType, "r"));
            }
        }
        else{
            output += emptySpacePrinter() + "__" + '\n';
        }
        emptySpaceCounter--;
    }

    public static void IfExpression() { // S
        output += emptySpacePrinter() + "<IfExpression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IF")) {
            output += emptySpacePrinter() + "IF (" + getActualLexeme() + ")\n";
            nextLine();
            Expression();
            Expression();
            EndExpression();
        }
        else {
            printError("IF");
        }
        emptySpaceCounter--;
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

