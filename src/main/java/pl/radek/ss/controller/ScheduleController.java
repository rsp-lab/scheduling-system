package pl.radek.ss.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;
import pl.radek.ss.lib.schedule.*;
import pl.radek.ss.dtos.UploadFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Controller
public class ScheduleController
{
    // -----------------------------------------------------------------------------------------------------
    private final PasswordEncoder passwordEncoder;
    
    public ScheduleController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public static final String SCHEDULE_PATH = System.getProperty("user.dir") + File.separator + "Schedule_";
    // -----------------------------------------------------------------------------------------------------
    @RequestMapping(value = "/user/schedule", method = RequestMethod.GET)
    public ModelAndView schedule() {
        ModelAndView mav = new ModelAndView("/user/schedule");
        return mav;
    }
    
    @RequestMapping(value = "/user/schedule", method = RequestMethod.POST)
    public ModelAndView schedule(@RequestParam(value = "fileParam") MultipartFile fileParam) throws UnsupportedEncodingException, IOException {
        ModelAndView mav = new ModelAndView("user/schedule");
        
        UploadFile uploadFile = new UploadFile(fileParam);
        
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(uploadFile.getFileData().getInputStream(), "UTF-8");
        try {
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0)
                    out.append(buffer, 0, read);
                
            } while (read >= 0);
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!out.toString().isEmpty()) {
            String xml = out.substring(1);
            
            // Wykonanie algorytmu
            String schedule = null;
            ScheduleParser parser = null;
            try {
                Configuration.getInstance().setupFromXML(xml);
                schedule = Algorithm.getInstance().start();
                
                String link = UUID.randomUUID().toString(); // passwordEncoder.encode(schedule);
                PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
                writer.println(schedule);
                writer.flush();
                
                uploadFile.setSchedule(schedule);
                uploadFile.setLink(link);
                
                parser = new ScheduleParser();
                
                parser.parseSchedule(schedule);
            }
            catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
            
            mav.addObject("mapGroup", parser.getGroupMap());
            mav.addObject("mapEvent", parser.getEventMap());
            mav.addObject("mapRoom", parser.getRoomMap());
            mav.addObject("mapTimeId", parser.getTimeIdMap());
            mav.addObject("mapTimeDay", parser.getTimeDayMap());
            mav.addObject("min_hour", Schedule.MIN_HOUR);
            mav.addObject("hours", Schedule.HOURS);
        }
        
        mav.addObject("uploadItem", uploadFile);
        return mav;
    }
    
    @RequestMapping(value = "/downloadSchedule", method = RequestMethod.GET)
    public void downloadSchedule(@RequestParam(value = "linkID") String linkID, HttpServletRequest request, HttpServletResponse response) {
        
        File file = new File(SCHEDULE_PATH + linkID);
        int length;
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=schedule.xml");
        
        byte[] bbuf = new byte[1024000];
        DataInputStream in;
        try {
            in = new DataInputStream(new FileInputStream(file));
            ServletOutputStream sot = response.getOutputStream();
            while ((length = in.read(bbuf)) > 0)
                sot.write(bbuf, 0, length);
            
            in.close();
            sot.flush();
            sot.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/saveSchedule", method = RequestMethod.GET)
    public ModelAndView saveSchedule(@RequestParam(value = "promoters") String promoters, @RequestParam(value = "courses") String courses, @RequestParam(value = "rooms") String rooms, @RequestParam(value = "groups") String groups, @RequestParam(value = "events") String events) {
        ModelAndView mav = new ModelAndView("user/schedule");
        
        List<Promoter> promotersList = new ArrayList<>();
        String[] promotersTab = promoters.split("_");
        
        for (int i = 0; i < promotersTab.length; i += 2) {
            Promoter professor = new Promoter(Integer.parseInt(promotersTab[i]), promotersTab[i + 1]);
            promotersList.add(professor);
        }
        
        List<Course> eventsList = new ArrayList<>();
        String[] eventsTab = courses.split("_");
        
        for (int i = 0; i < eventsTab.length; i += 2) {
            Course course = new Course(Integer.parseInt(eventsTab[i]), eventsTab[i + 1]);
            eventsList.add(course);
        }
        
        List<Room> roomsList = new ArrayList<>();
        String[] roomsTab = rooms.split("_");
        
        for (int i = 0; i < roomsTab.length; i += 3) {
            Room room = new Room(roomsTab[i], Boolean.parseBoolean(roomsTab[i + 1]),
                    Integer.parseInt(roomsTab[i + 2]));
            roomsList.add(room);
        }
        
        List<Group> groupsList = new ArrayList<>();
        String[] groupsTab = groups.split("_");
        
        for (int i = 0; i < groupsTab.length; i += 3) {
            Group group = new Group(Integer.parseInt(groupsTab[i]), groupsTab[i + 1],
                    Integer.parseInt(groupsTab[i + 2]));
            groupsList.add(group);
        }
        
        List<EventSchedule> classesList = new ArrayList<>();
        String[] classesTab = events.split("_");
        
        for (int i = 0; i < classesTab.length; i += 5) {
            Promoter promoter = null;
            Course course = null;
            
            for (Iterator<Promoter> iterator = promotersList.iterator(); iterator.hasNext(); ) {
                Promoter p = iterator.next();
                if (p.getId() == Integer.parseInt(classesTab[i]))
                    promoter = p;
            }
            
            for (Iterator<Course> iterator = eventsList.iterator(); iterator.hasNext(); ) {
                Course c = iterator.next();
                if (c.getId() == Integer.parseInt(classesTab[i + 1]))
                    course = c;
            }
            
            String[] groupsPerClass = classesTab[i + 2].split(",");
            List<Group> group = new ArrayList<>();
            for (int k = 0; k < groupsPerClass.length; k++) {
                for (Iterator<Group> iterator = groupsList.iterator(); iterator.hasNext(); ) {
                    Group g = iterator.next();
                    if (g.getId() == Integer.parseInt(groupsPerClass[k]))
                        group.add(g);
                }
            }
            
            EventSchedule es = new EventSchedule(promoter, course, group,
                    Boolean.parseBoolean(classesTab[i + 3]), Integer.parseInt(classesTab[i + 4]));
            classesList.add(es);
        }
        
        String schedule = null;
        ScheduleParser parser = null;
        UploadFile uploadFile = new UploadFile();
        try {
            for (Iterator<Promoter> iterator = promotersList.iterator(); iterator.hasNext(); ) {
                Promoter p = iterator.next();
                Configuration.getInstance().addPromoter(p);
            }
            for (Iterator<Course> iterator = eventsList.iterator(); iterator.hasNext(); ) {
                Course c = iterator.next();
                Configuration.getInstance().addCourse(c);
            }
            for (Iterator<Room> iterator = roomsList.iterator(); iterator.hasNext(); ) {
                Room r = iterator.next();
                Configuration.getInstance().addRoom(r);
            }
            for (Iterator<Group> iterator = groupsList.iterator(); iterator.hasNext(); ) {
                Group g = iterator.next();
                Configuration.getInstance().addGroup(g);
            }
            for (Iterator<EventSchedule> iterator = classesList.iterator(); iterator.hasNext(); ) {
                EventSchedule es = iterator.next();
                Configuration.getInstance().addCourseClass(es);
            }
            
            schedule = Algorithm.getInstance().start();
            
            String link = UUID.randomUUID().toString(); // passwordEncoder.encode(schedule);
            PrintWriter writer = new PrintWriter(SCHEDULE_PATH + link);
            writer.println(schedule);
            writer.flush();
            
            uploadFile.setSchedule(schedule);
            uploadFile.setLink(link);
            
            parser = new ScheduleParser();
            parser.parseSchedule(schedule);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        mav.addObject("uploadItem", uploadFile);
        mav.addObject("mapGroup", parser.getGroupMap());
        mav.addObject("mapEvent", parser.getEventMap());
        mav.addObject("mapRoom", parser.getRoomMap());
        mav.addObject("mapTimeId", parser.getTimeIdMap());
        mav.addObject("mapTimeDay", parser.getTimeDayMap());
        mav.addObject("min_hour", Schedule.MIN_HOUR);
        mav.addObject("hours", Schedule.HOURS);
        return mav;
    }
    // -----------------------------------------------------------------------------------------------------
}