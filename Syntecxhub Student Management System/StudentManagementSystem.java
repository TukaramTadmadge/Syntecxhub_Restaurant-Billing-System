import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

class Student {
    private int id;
    private String name;
    private int age;
    private String email;
    private String course;

    public Student(int id, String name, int age, String email, String course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.course = course;
    }

    // Getters & setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }

    // Convert to CSV row
    public String toCSV() {
        // escape commas simply by replacing them with space
        return id + "," + name.replace(",", " ") + "," + age + "," + email + "," + course.replace(",", " ");
    }

    public static Student fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 5) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int age = Integer.parseInt(parts[2].trim());
            String email = parts[3].trim();
            String course = parts[4].trim();
            return new Student(id, name, age, email, course);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

public class StudentManagementSystem {

    private final ArrayList<Student> students = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private final String DATA_FILE = "students.csv";

    public static void main(String[] args) {
        StudentManagementSystem app = new StudentManagementSystem();
        app.loadFromFile();
        app.run();
    }

    private void run() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addStudent(); break;
                case "2": viewAllStudents(); break;
                case "3": searchStudentById(); break;
                case "4": updateStudent(); break;
                case "5": deleteStudent(); break;
                case "6": saveToFile(); break;
                case "7": exit = confirmExit(); break;
                default: System.out.println("Invalid choice. Please enter 1-7."); break;
            }
            System.out.println();
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("===== Student Management System =====");
        System.out.println("1. Add student");
        System.out.println("2. View all students");
        System.out.println("3. Search student by ID");
        System.out.println("4. Update student");
        System.out.println("5. Delete student");
        System.out.println("6. Save now");
        System.out.println("7. Exit");
        System.out.print("Enter choice: ");
    }

    private void addStudent() {
        System.out.println("--- Add New Student ---");
        int id = readInt("Enter student ID (integer): ");
        if (findById(id) != null) {
            System.out.println("ID already exists. Use a unique ID.");
            return;
        }
        String name = readNonEmpty("Enter name: ");
        int age = readIntWithMin("Enter age: ", 1);
        String email = readEmail("Enter email: ");
        String course = readNonEmpty("Enter course: ");

        Student s = new Student(id, name, age, email, course);
        students.add(s);
        System.out.println("Student added successfully.");
    }

    private void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println("--- All Students ---");
        System.out.printf("%-6s %-20s %-5s %-25s %-15s%n", "ID", "Name", "Age", "Email", "Course");
        System.out.println("--------------------------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-6d %-20s %-5d %-25s %-15s%n",
                    s.getId(), s.getName(), s.getAge(), s.getEmail(), s.getCourse());
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total students: " + students.size());
    }

    private void searchStudentById() {
        int id = readInt("Enter student ID to search: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student with ID " + id + " not found.");
            return;
        }
        System.out.println("Student found:");
        System.out.printf("ID: %d%nName: %s%nAge: %d%nEmail: %s%nCourse: %s%n",
                s.getId(), s.getName(), s.getAge(), s.getEmail(), s.getCourse());
    }

    private void updateStudent() {
        int id = readInt("Enter student ID to update: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student with ID " + id + " not found.");
            return;
        }
        System.out.println("Leave input empty to keep current value.");

        System.out.println("Current name: " + s.getName());
        String name = readOptional("New name: ");
        if (!name.isEmpty()) s.setName(name);

        System.out.println("Current age: " + s.getAge());
        String ageStr = readOptional("New age: ");
        if (!ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if (age > 0) s.setAge(age);
                else System.out.println("Invalid age, keeping old value.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, keeping old value.");
            }
        }

        System.out.println("Current email: " + s.getEmail());
        String email = readOptional("New email: ");
        if (!email.isEmpty()) {
            if (isValidEmail(email)) s.setEmail(email);
            else System.out.println("Invalid email, keeping old value.");
        }

        System.out.println("Current course: " + s.getCourse());
        String course = readOptional("New course: ");
        if (!course.isEmpty()) s.setCourse(course);

        System.out.println("Student updated.");
    }

    private void deleteStudent() {
        int id = readInt("Enter student ID to delete: ");
        Student s = findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Are you sure you want to delete this student? (yes/no): ");
        String conf = scanner.nextLine().trim().toLowerCase();
        if (conf.equals("yes") || conf.equals("y")) {
            students.remove(s);
            System.out.println("Student deleted.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    // File handling (CSV)
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Student s : students) {
                pw.println(s.toCSV());
            }
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {
            // no data yet
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int loaded = 0;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromCSV(line);
                if (s != null) {
                    // avoid duplicates if file was edited manually
                    if (findById(s.getId()) == null) {
                        students.add(s);
                        loaded++;
                    }
                }
            }
            if (loaded > 0) System.out.println("Loaded " + loaded + " students from " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Helpers & validation
    private Student findById(int id) {
        for (Student s : students) if (s.getId() == id) return s;
        return null;
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = scanner.nextLine().trim();
            try {
                return Integer.parseInt(in);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private int readIntWithMin(String prompt, int min) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min) return val;
            System.out.println("Value must be at least " + min + ".");
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = scanner.nextLine().trim();
            if (!in.isEmpty()) return in;
            System.out.println("Input cannot be empty.");
        }
    }

    private String readOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = scanner.nextLine().trim();
            if (isValidEmail(email)) return email;
            System.out.println("Invalid email format. Example: name@example.com");
        }
    }

    private boolean isValidEmail(String email) {
        // very simple email check
        if (email == null || email.length() < 5) return false;
        String regex = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(regex, email);
    }

    private boolean confirmExit() {
        System.out.print("Save before exiting? (yes/no): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("yes") || ans.equals("y")) saveToFile();
        System.out.println("Exiting. Goodbye!");
        return true;
    }
}
