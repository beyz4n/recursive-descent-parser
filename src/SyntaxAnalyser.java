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
        String point = line.split(" ")[1];
        int row = Integer.parseInt(point.split(":")[0]);
        int col = Integer.parseInt(point.split(":")[1]);
        str = lexemes.get(row-1);
        str = str.substring(col-1);
        String identifier = "";
        int count = 0;
        while(count < str.length() && !(str.charAt(count) == ' ' || str.charAt(count) == '(' || str.charAt(count) == ')' ||
                str.charAt(count) == '[' || str.charAt(count) == ']' || str.charAt(count) == '{' || str.charAt(count) == '}' || str.charAt(count) == '~')){
            identifier += str.charAt(count);
            count++;
        }
        return identifier;
    }
    public static void Program() {
        output += emptySpacePrinter() + "<Program>" + "\n";
        emptySpaceCounter++;
        if (!fileFinished && (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB"))) {
            TopLevelForm();
            Program();
        }
        else {
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
        emptySpaceCounter--;
    }

    public static void TopLevelForm() {
        output += emptySpacePrinter() + "<TopLevelForm>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            nextLine();
            SecondLevelForm();

            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(")");
                }
                else{
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "())" + "\n";
                    nextLine();
                }
            }
            else {
                //TODO:
                printError(")");
            }
        }
        else {
            printError("(");
        }
        emptySpaceCounter--;
    }

    public static void SecondLevelForm() {
        output += emptySpacePrinter() + "<SecondLevelForm>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            nextLine();
            FunCall();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(")");
                }
                else{
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "())" + "\n";
                    //is it true or not!
                    nextLine();
                }
            }
            else {
                //TODO:
                printError(")");
            }
        }
        else {
            Definition();
        }
        emptySpaceCounter--;
    }

    public static void Definition() {
        output += emptySpacePrinter() + "<Definition>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("DEFINE")) {
            output += emptySpacePrinter() + "DEFINE (" + getActualLexeme() + ")\n";
            nextLine();
            DefinitionRight();
        } else {
            printError("DEFINE");
        }
        emptySpaceCounter--;
    }

    public static void DefinitionRight() {
        output += emptySpacePrinter() + "<DefinitionRight>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER")) {
            output += emptySpacePrinter() + "IDENTIFIER" + "\n";
            Expression();
        }
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            nextLine();
            if (line.startsWith("IDENTIFIER")) {
                output += emptySpacePrinter() + "IDENTIFIER" + "\n";
                nextLine();
                ArgList();
                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "())" + "\n";
                        nextLine();
                        Statements();
                    }
                    else {
                        printError(")");
                    }
                }
                else {
                    printError(")");
                }
            }
            else {
                printError("IDENTIFIER");
            }
        }
        else {
            printError("( or IDENTIFIER");
        }
        emptySpaceCounter--;
    }

    public static void FunCall() {
        output += emptySpacePrinter() + "<FunCall>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER")) {
            output += emptySpacePrinter() + "IDENTIFIER" + "\n";
            nextLine();
            Expressions();
        }
        else {
            printError("IDENTIFIER");
        }
        emptySpaceCounter--;
    }

    public static void Expressions() {
        output += emptySpacePrinter() + "<Expressions>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
            Expressions();
        }
        else{
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
        emptySpaceCounter--;
    }

    public static void Expr() {
        output += emptySpacePrinter() + "<Expr>" + "\n";
        emptySpaceCounter++;
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
        else if (line.startsWith("IDENTIFIER")) {
            FunCall();
        }
        else {
            printError("LET/COND/IF/BEGIN/IDENTIFIER");
        }
        emptySpaceCounter--;
    }

    public static void Expression() {
        output += emptySpacePrinter() + "<Expression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING")) {
            output += emptySpacePrinter() + line.split(" ")[0] + "\n";
            nextLine();
        }
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            nextLine();
            Expr();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(")");
                }
                else{
                    output +=  emptySpacePrinter() + "RIGHT" + bracketType + "())" + "\n";
                    nextLine();
                }
            }
            else{
                printError(")");
            }
        }
        else {
            printError("IDENTIFIER/NUMBER/CHAR/BOOLEAN/STRING/(");
        }
        emptySpaceCounter--;
    }

    public static void LetExpression() {
        output += emptySpacePrinter() + "<LetExpression>" + "\n";
        emptySpaceCounter++;
        if (line.startsWith("LET")) {
            output += emptySpacePrinter() + "LET" + "(" + ")" + "\n"; // içine eklenecek
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
            output += emptySpacePrinter() + "IDENTIFIER" + "(" + ")" + "\n"; // içine eklenecek
            nextLine();

            if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
                String bracketType = line.split(" ")[0];
                bracketType = bracketType.substring(4);
                output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
                nextLine();
                VarDefs();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
                        nextLine();
                        Statements();
                    }
                    else {
                        printError(")");
                    }
                }
                else {
                    printError(")");
                }
            }
            else {
                printError("(");
            }
        } else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
            nextLine();
            VarDefs();

            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
                    nextLine();
                    Statements();
                } else {
                    printError(")");
                }
            } else {
                printError(")");
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
            output += emptySpacePrinter() + "COND" + "(" + ")" + "\n"; // içine eklenecek
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
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
            nextLine();
            Expression();
            Statements();
            nextLine(); // şu sanki olmamalı ama(?)
            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
                    nextLine();
                    CondBranch();
                } else {
                    printError(")");
                }
            } else {
                printError(")");
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
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
            nextLine();
            Expression();
            Statements();
            nextLine(); // bu da olmamalı gibi ama (?)
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    printError(")");
                }
                else {
                    output += emptySpacePrinter() + "RIGHT" + bracketType + "(()" + "\n"; // bracked type düzenlenebilir
                }
            } else {
                printError(")");
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
            output += emptySpacePrinter() + "IF" + "(" + ")" + "\n"; // içine eklenecek
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
            output += emptySpacePrinter() + "BEGIN" + "\n";
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
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            if (line.startsWith("IDENTIFIER")) {
                output += emptySpacePrinter() + "IDENTIFIER" + "\n";
                nextLine();
                Expression();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        output += emptySpacePrinter() + "RIGHT" + bracketType + "())" + "\n";
                        VarDef();
                    }
                    else {
                        printError(")");
                    }
                }
                else {
                    printError(")");
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
            output += emptySpacePrinter() + "IDENTIFIER" + "\n";
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
            output += emptySpacePrinter() + "DEFINE" + "\n";
            Definition();
            Statements();
        } else {
            Expression();
        }
        emptySpaceCounter--;
    }
}

