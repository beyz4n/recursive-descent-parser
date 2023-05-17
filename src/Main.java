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
        if(line.contains("LEFTPAR")){
            SecondLevelForm();
            line = scanner1.nextLine();
            if(!line.contains("RIGHTPAR")) {
            //ERROR
            }
        }
        //ERROR
    }

    public static void SecondLevelForm(){
        if(line.contains("LEFTPAR")){
            line = scanner1.nextLine();
            FunCall();
            line = scanner1.nextLine();
            if(!line.contains("RIGHTPAR")) {
                //ERROR
            }
        }
        else{
            Definition();
        }
    }

    public static void Definition(){
        if(line.contains("DEFINE")){

        }
        else{
            // error
        }
    }

    public static void DefinitionRight(){

    }

    public static void FunCall(){

    }
    public static void Expressions(){

    }

    public static void Expr(){

    }

    public static void Expression(){

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

