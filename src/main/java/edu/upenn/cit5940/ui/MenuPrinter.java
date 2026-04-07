package edu.upenn.cit5940.ui;

public class MenuPrinter {
    public static void printMainMenu() {
        System.out.println("==================================================");
        System.out.println("               MAIN MENU");
        System.out.println("==================================================");
        System.out.println("1. Interactive Mode (Guided Menu)");
        System.out.println("2. Command Mode (Direct Commands)");
        System.out.println("3. Help & Documentation");
        System.out.println("4. Exit");
        System.out.println("==================================================");
        System.out.print("Please select an option (1-4): ");
    }

    public static void printInteractiveMenu() {
        System.out.println("==================================================");
        System.out.println("              INTERACTIVE MODE");
        System.out.println("==================================================");
        System.out.println("This mode will guide you through each operation step by step.");
        System.out.println("----------------------------------------");
        System.out.println("         AVAILABLE SERVICES");
        System.out.println("----------------------------------------");
        System.out.println("1. Search Articles");
        System.out.println("2. Get Autocomplete Suggestions");
        System.out.println("3. View Top Topics");
        System.out.println("4. Analyze Topic Trends");
        System.out.println("5. Browse Articles by Date");
        System.out.println("6. View Specific Article by ID");
        System.out.println("7. Show Statistics");
        System.out.println("8. Back to Main Menu");
        System.out.println("----------------------------------------");
        System.out.print("Select a service (1-8): ");
    }

    public static void printHelpMenu() {
        System.out.println("============================================================");
        System.out.println("                 HELP & DOCUMENTATION");
        System.out.println("============================================================");
        System.out.println("INTERACTIVE MODE:");
        System.out.println("  • Guided step-by-step interface");
        System.out.println("  • Prompts for all required inputs");
        System.out.println("  • Perfect for beginners");
        System.out.println();
        System.out.println("COMMAND MODE:");
        System.out.println("  • Direct command entry");
        System.out.println("  • Faster for experienced users");
        System.out.println("  • Type 'help' for command list");
        System.out.println();
        System.out.println("AVAILABLE SERVICES:");
        System.out.println("  1. Search Articles - Find articles by keywords");
        System.out.println("  2. Autocomplete - Get search suggestions");
        System.out.println("  3. Top Topics - View trending topics by period");
        System.out.println("  4. Topic Trends - Analyze topic popularity over time");
        System.out.println("  5. Browse Articles - Filter articles by date range");
        System.out.println("  6. View Article - Get detailed article information");
        System.out.println("  7. Statistics - View database statistics");
        System.out.println();
        System.out.println("DATE FORMATS:");
        System.out.println("  • Period: YYYY-MM (e.g., 2023-12)");
        System.out.println("  • Date: YYYY-MM-DD (e.g., 2023-12-01)");
        System.out.println();
        System.out.println("Press Enter to return to the main menu...");
    }

    public static void printCommandModeHeader() {
        System.out.println("==================================================");
        System.out.println("                 COMMAND MODE");
        System.out.println("==================================================");
        System.out.println("Enter commands directly. Type 'help' for available commands.");
        System.out.println("Type 'menu' to return to the main menu.");
        System.out.println();
    }

    public static void printCommandHelp() {
        System.out.println("Available commands:");
        System.out.println("search <keyword>");
        System.out.println("autocomplete <prefix>");
        System.out.println("topics <period>");
        System.out.println("trends <topic> <start> <end>");
        System.out.println("articles <start_date> <end_date>");
        System.out.println("article <id>");
        System.out.println("stats");
        System.out.println("help");
        System.out.println("menu");
    }
}