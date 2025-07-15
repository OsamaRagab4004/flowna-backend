package com.flowna.app.Logic.Prompts.Planner.General;

public class PrioritizeMilestones {


    public final static String CATEGORIZE_MILESTONES = """
Transform a structured text input containing tasks, milestones, and planned sessions into a reorganized list where all individual sessions are grouped by their deadline date and sorted chronologically. Each session listed in the output must retain all its original details.Input Format:The input will be structured text, likely using Markdown, with the following hierarchy:Tasks: Defined with a title and often an overall deadline (e.g., ### Task X: [Task Title] [YYYY-MM-DD]).Milestones: Nested under tasks, with a title and potentially overall details (e.g., #### Milestone Y: [Milestone Title]).Milestone Details: May include overall description, time estimation, priority, and complexity for the milestone itself.Sessions: Nested under milestones, representing the actual work blocks. Each session will have specific details:Title: A unique title identifying the milestone and session number (e.g., Milestone Y: [Milestone Title] (Session Z)).Description: The description of the work for that session (often inherited from the milestone).Time Estimation: The duration planned for that specific session (e.g., 45 minutes, 15 minutes).Priority: The priority level for that session (e.g., High, Medium, Low).Complexity: The complexity level for that session (e.g., Normal, Hard, Easy).Deadline: The specific deadline date for that session (e.g., YYYY-MM-DD).Output Requirements:Extract All Sessions: Identify and extract every individual session defined within the input milestones.Group by Deadline: Group the extracted sessions based on their Deadline date.Sort Chronologically: Order the date groups chronologically, starting with the earliest deadline date.Include Full Details per Session: For each session listed in the output, include the following information:Task Name: The name of the parent Task the session belongs to (e.g., "Task 1: Define Target Audience Segments").Session Title: The full title of the session, including the milestone name and session number, followed by the deadline in brackets (e.g., Milestone 1: Data Collation and Initial Segmentation (Session 1) [2025-02-12]).Description: The specific description associated with the session.Time Estimation: The time allocated specifically to this session.Priority: The priority level of this session.Complexity: The complexity level of this session.Formatting:Use clear headings for each Deadline date (e.g., **Deadline: YYYY-MM-DD**).Use bullet points to list each session under its corresponding date heading.Clearly list the required details (Task Name, Description, Time Estimation, Priority, Complexity) for each session, potentially using sub-bullets or a key-value format.Use separators (like ---) between date groups for better readability.Example:Input Example Snippet:### Task 1: Define Target Audience Segments [2025-02-12]

#### Milestone 1: Data Collation and Initial Segmentation
- Description: Gather and organize existing customer data for analysis and initial segmentation
- Time Estimation: 60 minutes
- Priority: High
- Complexity: Normal
- Sessions:
  - Title: Milestone 1: Data Collation and Initial Segmentation (Session 1)
    Description: Gather and organize existing customer data for analysis and initial segmentation
    Time Estimation: 45 minutes
    Priority: High
    Complexity: Normal
    Deadline: 2025-02-12

  - Title: Milestone 1: Data Collation and Initial Segmentation (Session 2)
    Description: Gather and organize existing customer data for analysis and initial segmentation
    Time Estimation: 15 minutes
    Priority: High
    Complexity: Normal
    Deadline: 2025-02-12

### Task 2: Create Social Media Content Calendar [2025-02-13]

#### Milestone 1: Platform-Specific Strategy Definition
- Description: Define strategies for LinkedIn and TikTok...
- Time Estimation: 60 minutes
- Priority: Medium
- Complexity: Normal
- Sessions:
  - Title: Milestone 1: Platform-Specific Strategy Definition (Session 1)
    Description: Define strategies for LinkedIn and TikTok...
    Time Estimation: 45 minutes
    Priority: Medium
    Complexity: Normal
    Deadline: 2025-02-13
Corresponding Output Example Snippet:**Deadline: 2025-02-12**

* **Title**: Milestone 1: Data Collation and Initial Segmentation (Session 1) [2025-02-12]
    * **Task**: Task 1: Define Target Audience Segments
    * **Description**: Gather and organize existing customer data for analysis and initial segmentation
    * **Time Estimation**: 45 minutes
    * **Priority**: High
    * **Complexity**: Normal

* **Title**: Milestone 1: Data Collation and Initial Segmentation (Session 2) [2025-02-12]
    * **Task**: Task 1: Define Target Audience Segments
    * **Description**: Gather and organize existing customer data for analysis and initial segmentation
    * **Time Estimation**: 15 minutes
    * **Priority**: High
    * **Complexity**: Normal

---

**Deadline: 2025-02-13**

* **Title**: Milestone 1: Platform-Specific Strategy Definition (Session 1) [2025-02-13]
    * **Task**: Task 2: Create Social Media Content Calendar
    * **Description**: Define strategies for LinkedIn and TikTok...
    * **Time Estimation**: 45 minutes
    * **Priority**: Medium
    * **Complexity**: Normal

---
Instruction: Please process the provided input text according to these instructions and generate the reorganized, detailed list of sessions grouped by deadline.```

            """;


}
