import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class SyntaxAnalyser {
    static String line;
    static Scanner scanner1;
    static PrintWriter printWriter;
    static boolean fileFinished = false;


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

        File output = new File("output.txt");
        printWriter = new PrintWriter(output);
        scanner1 = new Scanner(file);


        nextLine();
        Program();


    }

    public static void nextLine(){
        if(scanner1.hasNextLine()){
            line = scanner1.nextLine();
        }
        else{
            fileFinished = true;
        }
    }

    public static void Program() {
        if (!fileFinished) {
            TopLevelForm();
            Program();
        }
    }

    public static void TopLevelForm() {
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);

            nextLine();
            SecondLevelForm();

            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    System.out.println("error");
                }
                else{
                    nextLine();
                }
            } else {
                System.out.println("error");
            }

        } else {
            System.out.println("error");
        }
    }

    public static void SecondLevelForm() {
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            FunCall();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    System.out.println("error");
                }
            } else {
                System.out.println("error");
            }
        } else {
            Definition();
        }
    }

    public static void Definition() {
        if (line.startsWith("DEFINE")) {
            nextLine();
            DefinitionRight();
        } else {
            System.out.println("error");
        }
    }

    public static void DefinitionRight() {
        if (line.startsWith("IDENTIFIER")) {
            Expression();
        }
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            if (line.startsWith("IDENTIFIER")) {
                nextLine();
                ArgList();
                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        Statements();
                    } else {
                        System.out.println("error");
                    }
                } else {
                    System.out.println("error");
                }
            } else {
                System.out.println("error");
            }
        } else {
            System.out.println("error");
        }

    }

    public static void FunCall() {
        if (line.startsWith("IDENTIFIER")) {
            nextLine();
            Expressions();
        } else {
            System.out.println("error");
        }

    }

    public static void Expressions() {
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
            Expressions();
        }
    }

    public static void Expr() {
        if (line.startsWith("LET")) {
            LetExpression();
        } else if (line.startsWith("COND")) {
            CondExpression();
        } else if (line.startsWith("IF")) {
            IfExpression();
        } else if (line.startsWith("BEGIN")) {
            BeginExpression();
        } else if (line.startsWith("IDENTIFIER")) {
            FunCall();
        } else {
            System.out.println("error");
        }
    }

    public static void Expression() {
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING")) {
            nextLine();
        } else   if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            Expr();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    System.out.println("error");
                }
                else{
                    nextLine();
                }
            }
        } else {
            System.out.println("error");
        }
    }

    public static void LetExpression() {
        if (line.startsWith("LET")) {
            nextLine();
            LetExpr();
        } else {
            System.out.println("ERROR");
        }
    }

    public static void LetExpr() {
        if (line.startsWith("IDENTIFIER")) {
            nextLine();
            if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
                String bracketType = line.split(" ")[0];
                bracketType = bracketType.substring(4);
                nextLine();
                VarDefs();
                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        Statements();
                    } else {
                        System.out.println("ERROR");
                    }
                }
                else{
                    System.out.println("ERROR");
                }
            } else {
                System.out.println("ERROR");
            }
        } else if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            VarDefs();
            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    nextLine();
                    Statements();
                } else {
                    System.out.println("ERROR");
                }
            }
            else {
                System.out.println("ERROR");
            }
        } else {
            System.out.println("ERROR");
        }
    }

    public static void CondExpression() {
        if (line.startsWith("COND")) {
            nextLine();
            CondBranches();
        } else {
            System.out.println("ERROR");
        }

    }

    public static void CondBranches() {
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            Expression();
            Statements();
            nextLine();
            if (line.contains(bracketType)) {
                if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                    nextLine();
                    CondBranch();
                } else {
                    System.out.println("ERROR");
                }
            }
            else {
                System.out.println("ERROR");
            }
        } else {
            System.out.println("ERROR");
        }
    }

    public static void CondBranch() {
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();
            Expression();
            Statements();
            nextLine();
            if (line.contains(bracketType)) {
                if (!line.startsWith("RIGHTPAR") && !line.startsWith("RIGHTSQUAREB") && !line.startsWith("RIGHTCURLYB")) {
                    System.out.println("ERROR");
                }
            }
            else{
                System.out.println("ERROR");
            }
        }
    }

    public static void IfExpression() { // S
        if (line.startsWith("IF")) {
            nextLine();
            Expression();
            Expression();
            EndExpression();
        } else {
            System.out.println("ERROR");
        }

    }

    public static void EndExpression() {
        if (line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING") || line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            Expression();
        }
    }

    public static void BeginExpression() {

        if (line.startsWith("BEGIN")) {
            nextLine();
            Statements();
        } else {
            System.out.println("ERROR...");
        }
    }

    public static void VarDefs() {

        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            String bracketType = line.split(" ")[0];
            bracketType = bracketType.substring(4);
            nextLine();

            if (line.startsWith("IDENTIFIER")) {
                nextLine();
                Expression();

                if (line.contains(bracketType)) {
                    if (line.startsWith("RIGHTPAR") || line.startsWith("RIGHTSQUAREB") || line.startsWith("RIGHTCURLYB")) {
                        nextLine();
                        VarDef();
                    } else {
                        System.out.println("ERROR...");
                    }
                }
                else {
                    System.out.println("ERROR...");
                }
            } else {
                System.out.println("ERROR...");
            }
        }
    }

    public static void VarDef() {
        if (line.startsWith("LEFTPAR") || line.startsWith("LEFTSQUAREB") || line.startsWith("LEFTCURLYB")) {
            VarDefs();
        }
    }

    public static void ArgList() {
        if (line.startsWith("IDENTIFIER")) {
            nextLine();
            ArgList();
        }
    }

    public static void Statements() {
        if (line.startsWith("DEFINE")) {
            nextLine();
            Definition();
            Statements();
        } else {
            Expression();
        }
    }
}

