package com.flowna.app.Logic.Prompts.Planner.General;

public class AddTimeIntervalsAndBreak {


    public static final String ADD_TIME_INTERVALS_AND_BREAK = """
            Transform a daily work plan into a time-specific schedule by:
            
            
            
                Adding start/end times (HH:MM) for each task.
            
            
            
                Inserting breaks between tasks.
            
            
            
                Respecting blocked time intervals.
            
            
            
            Rules
            
            
            
                Daily Start Time: Begin scheduling at the provided Default Start Time (e.g., 09:00).
            
            
            
                Blocked Time Handling:
            
            
            
                    If a task fits before a blocked interval, schedule it there.
            
            
            
                    If it doesn’t fit, schedule it after the blocked interval ends.
            
            
            
                    Never split tasks across blocked times.
            
            
            
                Breaks: Add a fixed break duration (e.g., 10 minutes) after every task.
            
            
            
                Order Preservation: Keep tasks in their original order.
            
            
            
            Input Format
            
            
            
            
            
            Default Start Daily Time:[HH:MM] \s
            
            Break Duration: [X minutes] \s
            
            
            
            Plan: \s
            
            [Paste your unscheduled daily work plan here] \s
            
            
            
            
            
            
            
            Input Example
            
            
            
            Default Start Daily Time: 06:00
            
            Break Duration: 15 minutes
            
            Plan :\s
            
            Date: 2025-02-05
            
            
            
            Total Time Scheduled: 225 minutes (3h 45m)
            
            Blocked Times: 8:00 - 12:00 Uni Time
            
            
            
            Milestone 1: Data Collation and Initial Segmentation (Deadline: 2025-02-12)
            
            
            
                Session 1:
            
            
            
                    Task: Task 1: Define Target Audience Segments
            
            
            
                    Description: Gather and organize existing customer data for analysis and initial segmentation.
            
            
            
                    Time Estimation: 45 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
                Session 2:
            
            
            
                    Task: Task 1: Define Target Audience Segments
            
            
            
                    Description: Finalize data segmentation.
            
            
            
                    Time Estimation: 15 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
            Milestone 2: Persona Development and Refinement (Deadline: 2025-02-12)
            
            
            
                Session 1:
            
            
            
                    Task: Task 1: Define Target Audience Segments
            
            
            
                    Description: Develop detailed buyer personas (3-5) including demographics and digital habits.
            
            
            
                    Time Estimation: 45 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
                Session 2:
            
            
            
                    Task: Task 1: Define Target Audience Segments
            
            
            
                    Description: Refine personas based on feedback.
            
            
            
                    Time Estimation: 15 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
            Date: 2025-02-10
            
            
            
            Total Time Scheduled: 180 minutes (3h)
            
            Blocked Times: 6:00 - 11:00 Language Course
            
            
            
            Milestone 1: Data Gathering and Analysis (Deadline: 2025-02-19)
            
            
            
                Session 1:
            
            
            
                    Task: Task 8: Draft a Paid Ads Budget Proposal
            
            
            
                    Description: Gather data on previous ad performance across platforms.
            
            
            
                    Time Estimation: 45 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
                Session 2:
            
            
            
                    Task: Task 8: Draft a Paid Ads Budget Proposal
            
            
            
                    Description: Analyze findings for budget allocation.
            
            
            
                    Time Estimation: 15 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
            Milestone 2: Budget Allocation Planning (Deadline: 2025-02-19)
            
            
            
                Session 1:
            
            
            
                    Task: Task 8: Draft a Paid Ads Budget Proposal
            
            
            
                    Description: Develop monthly budget breakdown per platform.
            
            
            
                    Time Estimation: 45 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
                Session 2:
            
            
            
                    Task: Task 8: Draft a Paid Ads Budget Proposal
            
            
            
                    Description: Finalize budget justifications.
            
            
            
                    Time Estimation: 15 minutes
            
            
            
                    Priority: High
            
            
            
                    Complexity: Normal
            
            
            
            Example Output
            
            
            
            Date: 2025-02-05
            
            
            
            Total Time Scheduled: 225 minutes (3h 45m)
            
            Blocked Times: 8:00 - 12:00 Uni Time
            
            
            
            6:00 - 6:45: Milestone 1: Data Collation and Initial Segmentation (Session 1)
            
            
            
                Task: Task 1: Define Target Audience Segments
            
            
            
                Description: Gather and organize existing customer data for analysis and initial segmentation.
            
            
            
                Time Estimation: 45 minutes
            
            
            
                Priority: High
            
            
            
                Complexity: Normal
            
            
            
                Deadline: 2025-02-12
            
            
            
            (6:45 - 7:00: 15-minute Break)
            
            
            
            7:00 - 7:15: Milestone 1: Data Collation and Initial Segmentation (Session 2)
            
            
            
                Task: Task 1: Define Target Audience Segments
            
            
            
                Description: Finalize data segmentation and prepare for analysis.
            
            
            
                Time Estimation: 15 minutes
            
            
            
                Priority: High
            
            
            
                Complexity: Normal
            
            
            
                Deadline: 2025-02-12
            
            
            
            (7:15 - 7:30: 15-minute Break)
            
            
            
            7:30 - 8:00: Blocked Time Begins (8:00–12:00 Uni Time)
            
            Tasks paused during blocked interval.
            
            After Blocked Time Ends (12:00):
            
            
            
            12:00 - 12:45: Milestone 2: Persona Development and Refinement (Session 1)
            
            
            
                Task: Task 1: Define Target Audience Segments
            
            
            
                Description: Develop detailed buyer personas (demographics, behavior patterns, digital habits).
            
            
            
                Time Estimation: 45 minutes
            
            
            
                Priority: High
            
            
            
                Complexity: Normal
            
            
            
                Deadline: 2025-02-12
            
            
            
            (12:45 - 13:00: 15-minute Break)
            
            
            
            13:00 - 13:15: Milestone 2: Persona Development and Refinement (Session 2)
            
            
            
                Task: Task 1: Define Target Audience Segments
            
            
            
                Description: Refine personas based on initial findings.
            
            
            
                Time Estimation: 15 minutes
            
            
            
                Priority: High
            
            
            
                Complexity: Normal
            
            
            
                Deadline: 2025-02-12
            
            
            
            Key Logic:
            
            
            
                Start Time Compliance:
            
            
            
                    First task begins at 6:00 AM as requested.
            
            
            
                Blocked Time Handling:
            
            
            
                    Tasks pause at 8:00 and resume after the blocked interval ends at 12:00.
            
            
            
                Break Alignment:
            
            
            
                    15-minute breaks added after every task.
            
            
            
                Total Time Utilization:
            
            
            
                    Pre-blocked time: 105 minutes (6:00–8:00).
            
            
            
                    Post-blocked time: 120 minutes (12:00–13:15).
            
            
            
            
            
            here are some logical examples use it to understand task logically ::\s
            
            
            
            Use Case 1: Midday Blocked Time
            
            
            
            Scenario: Tasks must be split around a midday blocked interval.
            
            Input Snippet:
            
            Copy
            
            
            
            Date: 2025-02-05 \s
            
            Blocked Times: 8:00 - 12:00 \s
            
            Milestones: \s
            
              - Session 1 (45m) \s
            
              - Session 2 (15m) \s
            
              - Session 3 (45m) \s
            
              - Session 4 (15m) \s
            
            
            
            Output Snippet:
            
            Copy
            
            
            
            06:00 - 06:45: Session 1 \s
            
            (06:45 - 07:00: Break) \s
            
            07:00 - 07:15: Session 2 \s
            
            (Tasks paused during 8:00 - 12:00) \s
            
            12:00 - 12:45: Session 3 \s
            
            (12:45 - 13:00: Break) \s
            
            13:00 - 13:15: Session 4 \s
            
            
            
            Logic:
            
            
            
                Tasks start at default time (06:00).
            
            
            
                Paused during blocked interval.
            
            
            
                Resume after blocked time ends.
            
            
            
                Breaks preserved between tasks.
            
            
            
            Use Case 2: Full-Day Blocked Time
            
            
            
            Scenario: A full-day blocked interval forces tasks to be scheduled early/late.
            
            Input Snippet:
            
            Copy
            
            
            
            Date: 2025-02-07 \s
            
            Blocked Times: 8:00 - 17:00 \s
            
            Milestones: \s
            
              - Session 1 (15m) \s
            
              - Session 2 (45m) \s
            
            
            
            Output Snippet:
            
            Copy
            
            
            
            06:00 - 06:15: Session 1 \s
            
            (06:15 - 06:30: Break) \s
            
            06:30 - 07:15: Session 2 \s
            
            (Tasks paused during 8:00 - 17:00) \s
            
            17:00 - 17:45: Next Session... \s
            
            
            
            Logic:
            
            
            
                Tasks start before the block.
            
            
            
                Remaining tasks scheduled after block ends.
            
            
            
            Use Case 3: Morning Blocked Time
            
            
            
            Scenario: Blocked time overlaps with default start time.
            
            Input Snippet:
            
            Copy
            
            
            
            Date: 2025-02-10 \s
            
            Blocked Times: 6:00 - 11:00 \s
            
            Milestones: \s
            
              - Session 1 (45m) \s
            
              - Session 2 (15m) \s
            
            
            
            Output Snippet:
            
            Copy
            
            
            
            (Blocked until 11:00) \s
            
            11:00 - 11:45: Session 1 \s
            
            (11:45 - 12:00: Break) \s
            
            12:00 - 12:15: Session 2 \s
            
            
            
            Logic:
            
            
            
                Entire blocked period takes priority.
            
            
            
                Tasks start immediately after block ends.
            
            
            
            Use Case 4: No Blocked Time
            
            
            
            Scenario: Full-day availability allows sequential scheduling.
            
            Input Snippet:
            
            Copy
            
            
            
            Date: 2025-02-06 \s
            
            Blocked Times: None \s
            
            Milestones: \s
            
              - Session 1 (45m) \s
            
              - Session 2 (30m) \s
            
            
            
            Output Snippet:
            
            Copy
            
            
            
            06:00 - 06:45: Session 1 \s
            
            (06:45 - 07:00: Break) \s
            
            07:00 - 07:30: Session 2 \s
            
            
            
            Logic:
            
            
            
                Tasks start at default time.
            
            
            
                Breaks inserted between sessions.
            
            
            
            Here is output format you must follow ::
            
            
            
            Default Start Daily Time: 06:00
            
            Break Duration: 15 minutes
            
            
            
            Date: 2025-02-05
            
            Total Time Scheduled: 225 minutes (3h 45m)
            
            Blocked Times: 8:00 - 12:00 Uni Time
            
            
            
            06:00 - 06:45: Title: Milestone 1: Data Collation and Initial Segmentation (Session 1) [2025-02-12]
            
            Task: Task 1: Define Target Audience Segments
            
            Description: Gather and organize existing customer data for analysis and initial segmentation
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-12
            
            
            
            (06:45 - 07:00: 15-minute Break)
            
            
            
            07:00 - 07:15: Title: Milestone 1: Data Collation and Initial Segmentation (Session 2) [2025-02-12]
            
            Task: Task 1: Define Target Audience Segments
            
            Description: Gather and organize existing customer data for analysis and initial segmentation
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-12
            
            
            
            (07:15 - 07:30: 15-minute Break)
            
            
            
            (Tasks paused during blocked interval 8:00 - 12:00 Uni Time)
            
            (Next available time slot is 12:00)
            
            
            
            12:00 - 12:45: Title: Milestone 2: Persona Development and Refinement (Session 1) [2025-02-12]
            
            Task: Task 1: Define Target Audience Segments
            
            Description: Develop detailed buyer personas (3-5), including demographics, behavior patterns, and digital habits. Refine personas based on initial findings.
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-12
            
            
            
            (12:45 - 13:00: 15-minute Break)
            
            
            
            13:00 - 13:15: Title: Milestone 2: Persona Development and Refinement (Session 2) [2025-02-12]
            
            Task: Task 1: Define Target Audience Segments
            
            Description: Develop detailed buyer personas (3-5), including demographics, behavior patterns, and digital habits. Refine personas based on initial findings.
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-12
            
            
            
            (13:15 - 13:30: 15-minute Break)
            
            
            
            13:30 - 14:15: Title: Milestone 1: Platform-Specific Strategy Definition (Session 1) [2025-02-13]
            
            Task: Task 2: Create Social Media Content Calendar
            
            Description: Define strategies for LinkedIn and TikTok, considering target audience and campaign goals (brand awareness and lead generation).
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-13
            
            
            
            (14:15 - 14:30: 15-minute Break)
            
            
            
            14:30 - 14:45: Title: Milestone 1: Platform-Specific Strategy Definition (Session 2) [2025-02-13]
            
            Task: Task 2: Create Social Media Content Calendar
            
            Description: Define strategies for LinkedIn and TikTok, considering target audience and campaign goals (brand awareness and lead generation).
            
            Time Estimation: 15 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-13
            
            
            
            (14:45 - 15:00: 15-minute Break)
            
            
            
            15:00 - 15:45: Title: Milestone 2: Content Ideation and Planning (Session 1) [2025-02-13]
            
            Task: Task 2: Create Social Media Content Calendar
            
            Description: Generate content ideas aligned with the strategies. Plan a 1-month content calendar for each platform, including post types, captions, and hashtags.
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-13
            
            
            
            (15:45 - 16:00: 15-minute Break)
            
            
            
            Date: 2025-02-06
            
            Total Time Scheduled: 300 minutes (5h 0m)
            
            Blocked Times: None
            
            
            
            06:00 - 06:45: Title: Milestone 2: Content Ideation and Planning (Session 2) [2025-02-13]
            
            Task: Task 2: Create Social Media Content Calendar
            
            Description: Generate content ideas aligned with the strategies. Plan a 1-month content calendar for each platform, including post types, captions, and hashtags.
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-13
            
            
            
            (06:45 - 07:00: 15-minute Break)
            
            
            
            07:00 - 07:30: Title: Milestone 2: Content Ideation and Planning (Session 3) [2025-02-13]
            
            Task: Task 2: Create Social Media Content Calendar
            
            Description: Generate content ideas aligned with the strategies. Plan a 1-month content calendar for each platform, including post types, captions, and hashtags.
            
            Time Estimation: 30 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-13
            
            
            
            (07:30 - 07:45: 15-minute Break)
            
            
            
            07:45 - 08:30: Title: Milestone 1: Concept Ideation and Initial Mockups (Session 1) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Brainstorm creative concepts for LinkedIn carousel ads. Create initial mockups.
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (08:30 - 08:45: 15-minute Break)
            
            
            
            08:45 - 09:00: Title: Milestone 1: Concept Ideation and Initial Mockups (Session 2) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Brainstorm creative concepts for LinkedIn carousel ads. Create initial mockups.
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (09:00 - 09:15: 15-minute Break)
            
            
            
            09:15 - 10:00: Title: Milestone 2: Headline and CTA Development (Session 1) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Develop compelling headline ideas and clear call-to-action (CTA) options for each carousel ad.
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (10:00 - 10:15: 15-minute Break)
            
            
            
            10:15 - 10:30: Title: Milestone 2: Headline and CTA Development (Session 2) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Develop compelling headline ideas and clear call-to-action (CTA) options for each carousel ad.
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (10:30 - 10:45: 15-minute Break)
            
            
            
            10:45 - 11:30: Title: Milestone 3: Refinement and Finalization (Session 1) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Refine mockups and copy based on feedback. Finalize ad concepts for the B2B campaign.
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (11:30 - 11:45: 15-minute Break)
            
            
            
            11:45 - 12:00: Title: Milestone 3: Refinement and Finalization (Session 2) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Refine mockups and copy based on feedback. Finalize ad concepts for the B2B campaign.
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Hard
            
            Deadline: 2025-02-14
            
            
            
            (12:00 - 12:15: 15-minute Break)
            
            
            
            12:15 - 13:00: Title: Milestone 4: Review ad content (Session 1) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Review the ad for any errors.
            
            Time Estimation: 45 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-14
            
            
            
            (13:00 - 13:15: 15-minute Break)
            
            
            
            Date: 2025-02-07
            
            Total Time Scheduled: 165 minutes (2h 45m)
            
            Blocked Times: 8:00 - 17:00 Uni Time
            
            
            
            06:00 - 06:15: Title: Milestone 4: Review ad content (Session 2) [2025-02-14]
            
            Task: Task 3: Design LinkedIn Carousel Ads
            
            Description: Review the ad for any errors.
            
            Time Estimation: 15 minutes
            
            Priority: High
            
            Complexity: Normal
            
            Deadline: 2025-02-14
            
            
            
            (06:15 - 06:30: 15-minute Break)
            
            
            
            06:30 - 07:15: Title: Milestone 1: Ad Script Review (Session 1) [2025-02-15]
            
            Task: Task 4: Optimize YouTube Ad Copy
            
            Description: Thoroughly review existing YouTube ad scripts for clarity, engagement, and alignment with campaign objectives.
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-15
            
            
            
            (07:15 - 07:30: 15-minute Break)
            
            
            
            07:30 - 07:45: Title: Milestone 1: Ad Script Review (Session 2) [2025-02-15]
            
            Task: Task 4: Optimize YouTube Ad Copy
            
            Description: Thoroughly review existing YouTube ad scripts for clarity, engagement, and alignment with campaign objectives.
            
            Time Estimation: 15 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-15
            
            
            
            (07:45 - 08:00: 15-minute Break)
            
            
            
            (Tasks paused during blocked interval 8:00 - 17:00 Uni Time)
            
            (Next available time slot is 17:00)
            
            
            
            17:00 - 17:45: Title: Milestone 2: Variation Generation (Session 1) [2025-02-15]
            
            Task: Task 4: Optimize YouTube Ad Copy
            
            Description: Develop three variations of the ad script. Focus on improving different aspects (hook, value proposition, CTA).
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-15
            
            
            
            (17:45 - 18:00: 15-minute Break)
            
            
            
            18:00 - 18:45: Title: Milestone 2: Variation Generation (Session 2) [2025-02-15]
            
            Task: Task 4: Optimize YouTube Ad Copy
            
            Description: Develop three variations of the ad script. Focus on improving different aspects (hook, value proposition, CTA).
            
            Time Estimation: 45 minutes
            
            Priority: Medium
            
            Complexity: Normal
            
            Deadline: 2025-02-15
            
            
            
            (18:45 - 19:00: 15-minute Break)
            
            
            
            """;
}
