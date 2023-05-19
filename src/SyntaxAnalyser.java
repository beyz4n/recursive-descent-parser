import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class SyntaxAnalyser {
    static String line;
    static String output = "";
    static Scanner scanner1;
    static PrintWriter printWriter;
    static boolean fileFinished = false;
    static long emptySpaceCounter = 0;


    public static void main(String[] args) throws Exception {

        System.out.print("Enter the name of the input file: ");
        Scanner scanner = new Scanner(System.in);
        String inputFileName = scanner.nextLine();
        File file = new File(inputFileName);

        while (!file.exists()) {
            System.out.print("You entered a file which does not exist. Please enter a valid one: ");
            scanner = new Scanner(System.in);
            inputFileName = scanner.nextLine();
            file = new File(inputFileName);
        }

        File outputFile = new File("output.txt");
        printWriter = new PrintWriter(outputFile);

        new LexicalAnalyser(file);
        File lex = new File("lexical_output.txt");
        scanner1 = new Scanner(lex);
        nextLine();
        Program();
        printWriter.print(outputFile);
        System.out.print(output);
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
        System.out.println(output);
        System.exit(1);
    }

    public static void Program() {
        if (!fileFinished && (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB"))) {
            output += "<Program>" + "\n";
            TopLevelForm();
            Program();
        }
        else {
            output +=  emptySpacePrinter() +  "__" + '\n';
        }
    }

    public static void TopLevelForm() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<TopLevelForm>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<SecondLevelForm>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<Definition>" + "\n";
        if (line.startsWith("DEFINE")) {
            output += emptySpacePrinter() + "DEFINE" + "\n";
            nextLine();
            DefinitionRight();
        } else {
            printError("DEFINE");
        }
        emptySpaceCounter--;
    }

    public static void DefinitionRight() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<DefinitionRight>" + "\n";
        if (line.startsWith("IDENTIFIER")) {
            Expression();
        }
        else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            output += emptySpacePrinter() + "LEFT" + bracketType + "(()" + "\n";
            nextLine();
            if (line.startsWith("IDENTIFIER")) {
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<FunCall>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<Expressions>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<Expr>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<Expression>" + "\n";
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

    public static void LetExpression() { // S
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<LetExpression>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<LetExpr>" + "\n";

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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<CondExpression>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<CondBranches>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<CondBranch>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<IfExpression>" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<EndExpresssion>" + "\n";
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void BeginExpression() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<BeginExpression>" + "\n";
        if (line.startsWith("BEGIN")) {
            nextLine();
            output += emptySpacePrinter() + "<BEGIN>" + "\n";
            Statements();
        } else {
            printError("BEGIN");
        }
        emptySpaceCounter--;
    }

    public static void VarDefs() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<VarDefs>" + "\n";
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            output += emptySpacePrinter() + "<LEFT" + bracketType + ">(()" + "\n";
            if (line.startsWith("IDENTIFIER")) {
                output += emptySpacePrinter() + "<IDENTIFIER>" + "\n";
                nextLine();
                Expression();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        output += emptySpacePrinter() + "<RIGHT" + bracketType + ">())" + "\n";
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
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<VarDef>" + "\n";
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            VarDefs();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void ArgList() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<ArgList>" + "\n";
        if (line.startsWith("IDENTIFIER")) {
            nextLine();
            output += emptySpacePrinter() + "<IDENTIFIER>" + "\n";
            ArgList();
        }
        else{
            output += emptySpacePrinter() + "__" + "\n";
        }
        emptySpaceCounter--;
    }

    public static void Statements() {
        emptySpaceCounter++;
        output += emptySpacePrinter() + "<Statements>" + "\n";
        if (line.startsWith("DEFINE")) {
            nextLine();
            output += emptySpacePrinter() + "<DEFINE>" + "\n";
            Definition();
            Statements();
        } else {
            Expression();
        }
        emptySpaceCounter--;
    }
}

