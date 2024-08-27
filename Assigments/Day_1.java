package Java_Practice_D1;

import java.util.Scanner;

class Questions{

    int first_question(){
        /*
        Write a program to add 8 to number 2345 and then divide by 3 and now mod of the
        quoptient is taken with 5 and then multiply the resutlatn value by 5.
        Display the final Result
         */
        int num1= 2345 + 8;
        int num2 = num1/ 3;
        int num3 = num2 % 5;
        return num3*5;
    }

    int second_question(){
        // Above quesiton but with += , -= , *= etc...
        int num1= 2345 + 8;
        num1 /= 3;
        num1 %= 5;
        num1 *= 5;
        return num1;
    }

    int third_question(){
        //Number of girls getting grade A
        int total_students = 90;

        int grade_A_students = total_students/2;
        int grade_A_boys = 20;

        int grade_A_girls = grade_A_students - grade_A_boys;
        return grade_A_girls;
    }

    void fourth_question(){
        //Creating the Scanner OBJ before calling it.

        //Supressing the warning that OBJ1 might b

        // Don't close the Scanner Object it might be behave weirdly when called simantaenusly.
        @SuppressWarnings("resource")
        Scanner obj1 = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String name =  obj1.nextLine();
        System.out.println("Roll Number");
        int roll_num =  obj1.nextInt();
        obj1.nextLine();
        System.out.println("Field of Intreset: ");
        String foi = obj1.nextLine();

        System.out.println("Hey my name is "+name+"and my roll number is "+roll_num+"My Field of intreset is: "+foi);

        // CLosing the object is imp as well as throw an warning related to it.
    }

    void fifth_question(){

        @SuppressWarnings("resource")
        Scanner emp_obj = new Scanner(System.in);
        System.out.print("Salary of EMP: ");
        int salary_of_emp = emp_obj.nextInt();
        System.out.print("Service years of EMP is: ");
        int service_years = emp_obj.nextInt();

        if (service_years > 6) {
            System.out.println("Salary of the employee is " + (int)(salary_of_emp * 1.1));
        } else {
            System.out.println("Salary of the employee is " + (int)salary_of_emp);
        }
    }

    void sixth_question(){
        @SuppressWarnings("resource")
        Scanner marks_obj = new Scanner(System.in);
        System.out.println("Enter the Marks: ");
        int marks = marks_obj.nextInt();

        if(marks<=25){
            System.out.println("F Grade");
        }
        else if(marks <= 45 && marks > 25){
            System.out.println("E Grade");
        }
        else if(marks <= 50 && marks > 45){
            System.out.println("D Grade");
        }
        else if(marks <= 60 && marks > 50){
            System.out.println("C Grade");
        }
        else if(marks <= 80 && marks > 60){
            System.out.println("B Grade");
        }
        else if(marks > 80){
            System.out.println("A Grade");
        }
    }

    void seventh_question(){
        // Student attendence more than 70% then onl entry in class
        @SuppressWarnings("resource")
        Scanner user_input = new Scanner(System.in);

        System.out.println("Number of Classes: ");
        int num_of_classes = user_input.nextInt();
        System.out.println("Number of Classes Attendend: ");
        int classes_attended = user_input.nextInt();

        int percent_class_attended = num_of_classes/classes_attended;
        System.out.println("Number of % class attended: "+ percent_class_attended*100+"%");
        if(percent_class_attended > 0.7){
            System.out.println("You are allowed to sit in class");
        }else{
            System.out.println("You are NOT! allowed to sit in class");
        }
    }

    void seventh_question(boolean medical_condition){
        if(medical_condition){
            System.out.println("You are eleigble to sit in Exam");
        }else{
            System.out.println("You are NOT! eleigble to sit in Exam");
        }
    }

    void nineth_question(){
        Scanner user_input = new Scanner(System.in);

        int total_amount  =0;
        int products = 0;

        while(true){
            System.out.println("Product Number(1/2/3): ");
            int product_number = user_input.nextInt();
            System.out.println("Quantity Sold: ");
            int quantity_sold = user_input.nextInt();

            switch(product_number){
                case 1:
                    total_amount += 22.5 * quantity_sold;
                    products++;
                    break;
                case 2:
                    total_amount += 44.5 * quantity_sold;
                    products++;
                    break;
                case 3:
                    total_amount += 9.98 * quantity_sold;
                    products++;
                    break;
            }
            user_input.nextLine();
            System.out.println("Quit?(T/F) ");
            String is_quit = user_input.nextLine();
            if(is_quit.equals("T")){
                System.out.println("Total Amout is: "+total_amount+ "\n"+ "Total Products are: "+products);
                break;
            }
        }
    }

    void eleventh_question(){
        // Ande ka Funda
        Scanner user_input = new Scanner(System.in);

        System.out.println("Number of Eggs: ");
        int num_of_eggs = user_input.nextInt();

        int gross = (int)num_of_eggs/144;
        int dozens = (int)(num_of_eggs % 144) / 12;
        int remaing_eggs = (int)(num_of_eggs - (gross * 144 + dozens * 12));
        System.out.println("Gross are: "+gross+"\nDozens are: "+dozens+ "\nRemaning Eggs are: "+remaing_eggs);
    }

    void twelth_question(){
        class Calculator{
            int addition(int num1,int num2){
                return num1 + num2;
            }
            int subtraction(int num1,int num2){
                return num1 - num2;
            }
            int multiplication(int num1,int num2){
                return num1 * num2;
            }
            int dividation(int num1,int num2){
                return num1 / num2;
            }
        }
        Calculator obj1 = new Calculator();
        System.out.println(obj1.addition(1, 2));
        System.out.println(obj1.subtraction(1, 2));
        System.out.println(obj1.multiplication(1, 2));
        System.out.println(obj1.dividation(1, 2));
    }

    void thirteenth_question(){
        class Shape{
            int perimiter(int square_side){
                return square_side*4;
            }
            int area(int square_side){
                return square_side*square_side;
            }
            int perimiter(int rectangle_length, int rectangle_breath){
                return 2*(rectangle_length +  rectangle_breath);
            }
            int area(int rectangle_length, int rectangle_breath){
                return rectangle_breath*rectangle_length;
            }
        }
        Shape obj1 = new Shape();
        System.out.println(obj1.area(4));
        System.out.println(obj1.perimiter(4));
        System.out.println(obj1.area(4,5));
        System.out.println(obj1.perimiter(4,5));
    }

    void fourtheen_question(){
        Scanner user_input = new Scanner(System.in);
        class Students{
            int iter = 1;
            int average_of_stud(int number_of_students){
                int total_marks = 0;
                while(iter <= number_of_students){
                    System.out.println("Marks for the Student"+ iter++);
                    int marks = user_input.nextInt();
                    if(marks < 0 || marks > 100){
                        System.out.println("Inavlid marks please Input the marks again.");
                        iter--;
                        continue;
                    }
                    total_marks += marks;
                }
                return total_marks;
            }
        }
        System.out.println("Number of Student");
        int number_of_studs = user_input.nextInt();
        Students obj2 = new Students();
        System.out.println("Average of Students are: " + obj2.average_of_stud(number_of_studs)/number_of_studs);
    }

    void fifteen_question(){
        Scanner user_input = new Scanner(System.in);
        System.out.println("Input the size of the Array: ");
        int size_of_array = user_input.nextInt();
        int[] arr = new int[size_of_array];
        System.out.println("Input the Elements in the Array");
        for(int i= 0 ; i <size_of_array ; i++){
            arr[i] = user_input.nextInt();
        }
        System.out.println("Which Element Occurance? ");
        int element_in_array = user_input.nextInt();
        int occur = 0;
        for(int i= 0 ; i <size_of_array ; i++){
            if(element_in_array == arr[i]){
                occur++;
            }
        }
        System.out.println("Number of Occurance of elmenet: "+element_in_array+" is "+occur);
    }
};

class Main{
    public static void main(String[] args){
        Questions question_d1 = new Questions();

        // System.out.println(question_d1.first_question());
        // System.out.println(question_d1.second_question());
        // System.out.println(question_d1.third_question());
        // question_d1.fourth_question();
        // question_d1.fifth_question();
        // question_d1.sixth_question();
        // question_d1.seventh_question();
        // question_d1.seventh_question(true);
        //question_d1.nineth_question();
        // question_d1.eleventh_question();
        // question_d1.twelth_question();
        // question_d1.thirteenth_question();
        // question_d1.fourtheen_question();
        question_d1.fifteen_question();
    }
}