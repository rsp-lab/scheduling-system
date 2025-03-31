# Scheduling System

System automating the process of selecting an optimal single event time or an entire event schedule. It is useful as a:
1. Survey system for selecting a single best event time for a group of users. At the end of the process, it generates a event link for all participants and allows them to chat.
2. Tool for creating an optimal schedule for multiple events. It is helpful for organizing conferences, managing schedules, and generating class timetables for universities.

![Scheduling System 1 Image](/scheduling-system1.png?raw=false)
_Example of creating a single event_

The problem of selecting an optimal event time is a P-type problem.
In the "best event date and time" tab for the event, you can find the best time slot in which the event can take place, assuming the event has multiple participants.
Each participant, when signing up, can provide a time period for the event that suits them the most.

In my application, I focused my efforts on the second use case, which is creating a event schedule — an optimization problem that is much more complex (NP-complete).
To solve this, I used a genetic algorithm, where the search process involves mechanisms such as evolution, natural selection, crossover, and mutation.

![Scheduling System 2 Image](/scheduling-system2.png?raw=false)
_Example of creating a meeting schedule, in this case, a university timetable_

To generate an Event Schedule, you need to select the "Create schedule" tab and upload an XML file, an example of which can be found in sampleScheduleXML/schedule_conf.xml.
The event schedule can be viewed in the app or downloaded in XML format.

Implemented in: Java, Spring, Hibernate, H2, XStream, JSP

### Usage

Run in target directory:
> java -jar scheduling-system-1.0.war