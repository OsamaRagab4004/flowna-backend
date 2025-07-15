package com.flowna.app.Logic.Prompts.Planner.General;

public class DefineDaysWithCapacity {


    public static final String DEFINE_DAYS_WITH_CAPACITY = """
            Prompt for Generating Daily Study Capacity Schedule
            
            
            
            Objective:
            
            
            
            To process user-defined study constraints and generate a daily schedule outlining the available study capacity (in hours) for each day within a specified range. The calculation must strictly follow the logic provided regarding minimum/maximum hours, off days, and the impact of blocked time.
            
            
            
            Input Requirements:
            
            
            
            The user must provide the following information:
            
            
            
                Maximum Study Hours per Day (Y): The maximum number of hours the user intends to study on a day with minimal or no blocked time (e.g., 6h).
            
            
            
                Minimum Study Hours per Day (X): The minimum number of hours the user commits to studying on a day, even if heavily blocked (unless it's an off day) (e.g., 2h).
            
            
            
                Start Date: The first date for which the schedule should be generated (e.g., 15.04.2025).
            
            
            
                End Date : End Date  A specific date marking the end of the scheduling period (e.g., 30.04.2025).
            
            
            
                Blocked Times/Events: A list specifying dates and time ranges when the user is unavailable. Each entry should include the date and the start/end times (e.g., 16.04.2025: 8:00 - 12:00, 17.04.2025: 14:00 - 17:30).
            
            
            
                Off Days: A list of specific dates designated as non-study days (e.g., 18.04.2025, 19.04.2025).
            
            
            
            Output Format:
            
            
            
            For each day from the Start Date to the determined End Date (or latest deadline), generate an entry in the following format:
            
            
            
            Date: [DD.MM.YYYY]
            
            Capacity: [Calculated Hours] hours
            
            Blocked Times: [List of blocked intervals for the day, e.g., "9:00 - 10:30", "14:00 - 16:00" or "None"]
            
            
            
            
            
            Use a separator (like ##### ) between daily entries for clarity.
            
            
            
            Detailed Logic for Capacity Calculation:
            
            
            
            Iterate through each date from the Start Date to the End Date (or the latest task deadline if provided). For each Current Date:
            
            
            
                Check for Off Day:
            
            
            
                    If Current Date is listed in Off Days:
            
            
            
                        Set Capacity = 0 hours.
            
            
            
                        List any Blocked Times specified for this date.
            
            
            
                        Proceed to the next date.
            
            
            
                Calculate Total Blocked Hours (B):
            
            
            
                    If not an Off Day, retrieve all Blocked Times specified for the Current Date.
            
            
            
                    Sum the duration of these blocked intervals to get the Total Blocked Hours (B). Handle time calculations accurately (e.g., 9:00 AM - 10:30 AM is 1.5 hours).
            
            
            
                Determine Daily Capacity based on Blocked Hours (B):
            
            
            
                    Case 1: High Block (B >= 7 hours)
            
            
            
                        Set Capacity = X hours (Minimum Study Hours).
            
            
            
                    Case 2: Low/No Block (B < 3 hours)
            
            
            
                        Set Capacity = Y hours (Maximum Study Hours).
            
            
            
            Case 3: Medium Block (3 <= B < 7 hours)
            
            
            
                Set Capacity =  [ X , (Y-2)] hours, e.g. Capacity = [ X , (Y-2)] hours
            
            
            
                Check if Capacity + Blocked Time is > 24 hours which is a logical error. in that case set Capacity any number between X and (Y-2) fit this equation : Capacity + Blocked Time <= 24.
            
            
            
                If there is no number between X and (Y-2) fit this equation : Capacity + Blocked Time <= 24. Set Capacity = 0
            
            
            
                Format Output:
            
            
            
                    Record the Current Date.
            
            
            
                    Record the calculated Capacity.
            
            
            
                    List the Blocked Times for the Current Date.
            
            
            
            Example:
            
            
            
            Input:
            
            
            
                Maximum Study Hours per Day (Y): 6h
            
            
            
                Minimum Study Hours per Day (X): 2h
            
            
            
                Start Date: 15.04.2025
            
            
            
                End Date : 19.04.2025
            
            
            
                Blocked Times:
            
            
            
                    15.04.2025: 9:00 - 10:00  going to GTM
            
            
            
                    16.04.2025: 8:00 - 12:00, 14:00 - 16:00 Uni Time
            
            
            
                    17.04.2025: 8:00 - 17:00 Uni Time
            
            
            
                    19.04.2025: 10:00 - 13:00  Language Course
            
            
            
                Off Days: 18.04.2025
            
            
            
            Expected Output:
            
            
            
            Date: 15.04.2025
            
            Capacity: 6 hours
            
            Blocked Times: 9:00 - 10:00 going to GTM\s
            
            ---
            
            Date: 16.04.2025
            
            Capacity: [2,4] hours
            
            Blocked Times: 8:00 - 12:00, 14:00 - 16:00 Uni Time
            
            ---
            
            Date: 17.04.2025
            
            Capacity: 2 hours
            
            Blocked Times: 8:00 - 17:00 Uni Time
            
            ---
            
            Date: 18.04.2025
            
            Capacity: 0 hours
            
            Blocked Times: None
            
            ---
            
            Date: 19.04.2025
            
            Capacity: [2,4] hours
            
            Blocked Times: 10:00 - 13:00  Language Course
            
            ---
            
            
            
            
            
            Instruction: Please process the user's input according to these detailed instructions and generate the daily capacity schedule. Ensure calculations for blocked hours and the application of the capacity logic (especially for medium block days) are precise.
            """;
}
