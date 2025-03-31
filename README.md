# Scheduling System

System automating the process of selecting an optimal single meeting time or an entire meeting schedule. It is useful as a:
1. Survey system for selecting a single best meeting time for a group of users. At the end of the process, it generates a meeting link for all participants and also allows them to chat.
2. Tool for creating an optimal schedule for multiple meetings. It is helpful for organizing conferences, managing schedules, and generating class timetables at universities.

![Scheduling System 1 Image](/scheduling-system1.png?raw=false)
_Example of creating a single meeting time_

The problem of selecting an optimal meeting time is a P-type problem. In my application, I focused my efforts on the second use case, which is creating a meeting schedule—an optimization problem that is much more complex (NP-complete). To solve this, I used a genetic algorithm, where the search process involves mechanisms such as evolution, natural selection, crossover, and mutation.

![Scheduling System 2 Image](/scheduling-system2.png?raw=false)
_Example of creating a meeting schedule, in this case, a university timetable_

Such a schedule can be created within the application itself or by uploading a schedule file in XML format.

Implemented in: Java, Spring, Hibernate, PostgreSQL, XStream, JSP
