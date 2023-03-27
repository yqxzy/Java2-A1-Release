
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;



/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

  List<Course> courses = new ArrayList<>();

  public OnlineCoursesAnalyzer(String datasetPath) {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                Double.parseDouble(info[21]), Double.parseDouble(info[22]));
        courses.add(course);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //1
  public Map<String, Integer> getPtcpCountByInst() {
    Map<String, Integer> map = courses.stream().collect(Collectors.groupingBy(x-> x.institution, TreeMap::new, Collectors.summingInt(x-> x.participants)));
    return map;
  }

  //2
  public Map<String, Integer> getPtcpCountByInstAndSubject() {
    Map<String, Integer> map1 = courses.stream().collect(Collectors.groupingBy(x-> x.institution+ '-' + x.subject, Collectors.summingInt(x-> x.participants)));
    Map<String, Integer> map = new LinkedHashMap<>();
    map1.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEachOrdered(x-> map.put(x.getKey(), x.getValue()));
    return map;
  }

  //3
  public Map<String, List<List<String>>> getCourseListOfInstructor() {
    courses.sort(Comparator.comparing(o->o.title));
    Map<String, List<String>> map = courses.stream().collect(Collectors.groupingBy(x-> x.instructors, Collectors.mapping(x-> x.title, Collectors.toList())));
    Map<String, List<List<String>>> xmap = new LinkedHashMap<>();
    Map<String, List<String>> map1 = new TreeMap<>();
    Map<String, List<String>> map2 = new TreeMap<>();
    map.entrySet().forEach(x-> {
         if(x.getKey().contains(",")) {
             String[] s= x.getKey().split(",");
             for(int i=0;i<s.length;i++) {
                 s[i]=s[i].trim();
                 if(map1.containsKey(s[i])) {
                     List<String> temp=map1.get(s[i]);
                     for(String ss:x.getValue()) {
                         if(!temp.contains(ss)) temp.add(ss);
                     }
                     temp.sort(Comparator.comparing(o->o));
                     temp=temp.stream().distinct().collect(Collectors.toList());
                     map1.put(s[i], temp);
                 }else {
                     map1.put(s[i], x.getValue(). stream().distinct().collect(Collectors.toList()));
                    }
             }
         }else {
             if(map2.containsKey(x.getKey())) {
                 List<String> temp=map1.get(x.getKey());
                 for(String ss: x.getValue()) {
                     if(!temp.contains(ss)) temp.add(ss);
                 }
                 temp.sort(Comparator.comparing(o-> o));
                 temp=temp.stream().distinct().collect(Collectors.toList());
                 map2.put(x.getKey(), temp);
             }else {
                 map2.put(x.getKey(), x.getValue().stream().distinct().collect(Collectors.toList()));
             }
         }
     });
     map1.entrySet().forEach(x->{
         if(map2.containsKey(x.getKey())){
             List<List<String>> temp=new ArrayList<>();
             temp.add(map2.get(x.getKey()));
             temp.add(x.getValue());
             xmap.put(x.getKey(),temp);
         }else {
             List<List<String>> temp=new ArrayList<>();
             List<String>li=new ArrayList<>();
             temp.add(li);
             temp.add(x.getValue());
             xmap.put(x.getKey(),temp);
         }
     });
     map2.entrySet().forEach(x->{
         if(!map1.containsKey(x.getKey())){
             List<List<String>> temp=new ArrayList<>();
             List<String>li=new ArrayList<>();
             temp.add(x.getValue());
             temp.add(li);
             xmap.put(x.getKey(),temp);
         }
     });
     return xmap;
  }

    //4
  public List<String> getCourses(int topK, String by) {
    List<String> list=new ArrayList<>();
    courses.sort(Comparator.comparing(o->o.title));
    if(by.equals("hours")){
        courses.sort(Comparator.comparing(o->o.totalHours));
        int f=0;
        for(int i=courses.size()-1;f==0;i--){
            if(!list.contains(courses.get(i).title)) list.add(courses.get(i).title);
            if(list.size()>=topK&&courses.get(i-1).title!=courses.get(i).title) f=1;
        }
    }else if(by.equals("participants")){
        courses.sort(Comparator.comparing(o->o.participants));
        int f=0;
        for(int i=courses.size()-1;f==0;i--){
            if(!list.contains(courses.get(i).title)) list.add(courses.get(i).title);
            if(list.size()>=topK&&courses.get(i-1).title!=courses.get(i).title) f=1;
        }
    }
    return list;
  }

  //5
  public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
    List<String> li=new ArrayList<>();
    courses.sort(Comparator.comparing(o->o.title));
    for(Course co:courses){
        if(co.totalHours<=totalCourseHours&&co.percentAudited>=percentAudited) {
            String s=courseSubject.toLowerCase(Locale.ROOT);
            if(co.subject.toLowerCase(Locale.ROOT).contains(s)){
                if(!li.contains(co.title))li.add(co.title);
            }
        }
    }
    return li;
  }

  //6
  public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
    Map<String,Double> aveAge=courses.stream().collect(Collectors.groupingBy(x->x.number, Collectors.averagingDouble(x->x.medianAge)));
    Map<String,Double> aveGender=courses.stream().collect(Collectors.groupingBy(x->x.number, Collectors.averagingDouble(x->x.percentMale)));
    Map<String,Double> aveBachelor=courses.stream().collect(Collectors.groupingBy(x->x.number, Collectors.averagingDouble(x->x.percentDegree)));
    Map<String,Double> similarValue=new HashMap<>();
    aveAge.entrySet().forEach(x->{
        Double value= Math.pow(age-x.getValue(),2)+Math.pow(gender*100-aveGender.get(x.getKey()),2)+Math.pow(isBachelorOrHigher*100-aveBachelor.get(x.getKey()),2);
        similarValue.put(x.getKey(),value);
    });
    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(similarValue.entrySet());
    Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
        public int compare(Map.Entry<String,Double> o1, Map.Entry<String, Double> o2) {
            if(o1.getValue()-o2.getValue()>0){
                return 1;
            }else if(o1.getValue()-o2.getValue()==0){
                return 0;
            }
            return -1;
        }
    });
    courses.sort(Comparator.comparing((Course o)->o.launchDate).reversed());
    Map<String, List<String>> map = courses.stream().collect(Collectors.groupingBy(x->x.number,Collectors.mapping(x->x.title,Collectors.toList())));
    List<String> li =new ArrayList<>();
    List<String> temp =new ArrayList<>();
    List<Double> va=new ArrayList<>();
    for(int i=0;i<list.size();i++){
        if(li.size()==10&&(list.get(i).getValue()!=list.get(i+1).getValue())){
            break;
        }
        if(!li.contains(map.get(list.get(i).getKey()).get(0))){
            li.add(map.get(list.get(i).getKey()).get(0));
            va.add(list.get(i).getValue());
        }
    }
    List<String> reList =new ArrayList<>();
    double p=-10.0;
    for(int i=0;i<va.size();i++){
       if(va.get(i)!=p){
           p=va.get(i);
           Collections.sort(temp);
           reList.addAll(temp);
           temp.clear();
           temp.add(li.get(i));
      }else {
        temp.add(li.get(i));
      }
    }
    reList.addAll(temp);
    return reList;
  }
}

class Course {
  String institution;
  String number;
  Date launchDate;
  String title;
  String instructors;
  String subject;
  int year;
  int honorCode;
  int participants;
  int audited;
  int certified;
  double percentAudited;
  double percentCertified;
  double percentCertified50;
  double percentVideo;
  double percentForum;
  double gradeHigherZero;
  double totalHours;
  double medianHoursCertification;
  double medianAge;
  double percentMale;
  double percentFemale;
  double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
    this.institution = institution;
    this.number = number;
    this.launchDate = launchDate;
    if (title.startsWith("\"")) title = title.substring(1);
    if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
    this.title = title;
    if (instructors.startsWith("\"")) instructors = instructors.substring(1);
    if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
    this.instructors = instructors;
    if (subject.startsWith("\"")) subject = subject.substring(1);
    if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
    this.subject = subject;
    this.year = year;
    this.honorCode = honorCode;
    this.participants = participants;
    this.audited = audited;
    this.certified = certified;
    this.percentAudited = percentAudited;
    this.percentCertified = percentCertified;
    this.percentCertified50 = percentCertified50;
    this.percentVideo = percentVideo;
    this.percentForum = percentForum;
    this.gradeHigherZero = gradeHigherZero;
    this.totalHours = totalHours;
    this.medianHoursCertification = medianHoursCertification;
    this.medianAge = medianAge;
    this.percentMale = percentMale;
    this.percentFemale = percentFemale;
    this.percentDegree = percentDegree;
  }
}