import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    static String line;
    static Scanner scanner1;
    static PrintWriter printWriter;

    public static void main(String[] args) throws Exception {

        System.out.print("Enter the name of the input file: ");
        Scanner scanner = new Scanner(System.in);
        String inputFileName = scanner.nextLine();
        File file = new File(inputFileName);

        while (!file.exists()){
            System.out.print("You entered a file which does not exist. Please enter a valid one: ");
            scanner = new Scanner(System.in);
            inputFileName = scanner.nextLine();
            file = new File(inputFileName);
        }

        File output = new File("output.txt");
        printWriter = new PrintWriter(output);
        Scanner scanner1 = new Scanner(file);

        while(scanner1.hasNextLine()){
            line = scanner1.nextLine();
            Program();
        }

    }

    public static void Program(){
        if(!line.isEmpty()) {
            TopLevelForm();
            Program();
        }
    }

    public static void TopLevelForm(){
        if(line.startsWith("LEFTPAR")){
            line = scanner1.nextLine();
            SecondLevelForm();
            line = scanner1.nextLine();
            if(!line.startsWith("RIGHTPAR")) {
                System.out.println("error");
            }
        }
        System.out.println("error");
    }

    public static void SecondLevelForm(){
        if(line.startsWith("LEFTPAR")){
            line = scanner1.nextLine();
            FunCall();
            line = scanner1.nextLine();
            if(!line.startsWith("RIGHTPAR")) {
                System.out.println("error");
            }
        }
        else{
            Definition();
        }
    }

    public static void Definition(){
        if(line.startsWith("DEFINE")){
            line = scanner1.nextLine();
            DefinitionRight();
        }
        else{
            System.out.println("error");
        }
    }

    public static void DefinitionRight(){
        if(line.startsWith("IDENTIFIER")){
            Expression();
        }
        else if (line.startsWith("LEFTPAR")) {
            line = scanner1.nextLine();
            if(line.startsWith("IDENTIFIER")){
                line = scanner1.nextLine();
                ArgList();
                line = scanner1.nextLine();
                if(line.startsWith("RIGHTPAR")){
                    line = scanner1.nextLine();
                    Statements();
                }
                else{
                    System.out.println("error");
                }
            }
            else{
                System.out.println("error");
            }
        }
        else{
            System.out.println("error");
        }

    }

    public static void FunCall(){
        if(line.startsWith("IDENTIFIER")){
            line = scanner1.nextLine();
            Expressions();
        }
        else{
            System.out.println("error");
        }

    }
    public static void Expressions(){
        if(!line.isEmpty()){
            Expression();
            line = scanner1.nextLine();
            Expressions();
        }
    }

    public static void Expr(){
        if(line.startsWith("LET")){
            LetExpression();
        }
        else if (line.startsWith("COND")) {
            CondExpression();
        }
        else if (line.startsWith("IF")){
            IfExpression();
        }
        else if (line.startsWith("BEGIN")) {
            BeginExpression();
        }
        else if (line.startsWith("IDENTIFIER")){
            FunCall();
        }
        else{
            System.out.println("error");
        }
    }

    public static void Expression(){
        if(line.startsWith("IDENTIFIER") || line.startsWith("NUMBER") || line.startsWith("CHAR") || line.startsWith("BOOLEAN") || line.startsWith("STRING")) {
            line = scanner1.nextLine();
        }
        else if(line.startsWith("LEFTPAR")){
            line = scanner1.nextLine();
            Expr();
            line = scanner1.nextLine();
            if(!line.startsWith("RIGHTPAR")){
                System.out.println("error");
            }
        }
        else{
            System.out.println("error");
        }
    }

    public static void LetExpression(){

    }

    public static void LetExpr(){

    }

    public static void CondExpression(){

    }
    public static void CondBranches(){

    }

    public static void CondBranch(){

    }
    public static void IfExpression(){

    }
    public static void EndExpression(){

    }
    public static void BeginExpression(){

    }

    public static void VarDefs(){

    }

    public static void VarDef(){

    }

    public static void ArgList(){

    }

    public static void Statements(){

    }

}

