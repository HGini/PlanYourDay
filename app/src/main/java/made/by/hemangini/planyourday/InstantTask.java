package made.by.hemangini.planyourday;


import java.util.GregorianCalendar;



public class InstantTask {
    private String Note;
    private String Place;
    private GregorianCalendar Date;
    private Time time;

    public InstantTask(){}

    public InstantTask(GregorianCalendar Date, Time time, String Place, String Note){
        this.Note = Note;
        this.Place =Place;
        this.Date = Date;
        this.time = time;
    }



    //get and set methods
    public GregorianCalendar getDate(){return this.Date;}

    
    public void setDate(GregorianCalendar Date){
        this.Date = Date;        
    }
    
    public String getNote(){
        return this.Note;
    }

    public void setNote(String Note){
        this.Note = Note;
    }

    public String getPlace(){
        return this.Place;
    }

    public void setPlace(String Place){
        this.Place = Place;
    }

    public Time getTime(){ return this.time;}

    public int getTimeHour(){
        return this.time.hours;
    }

    public int getTimeMinute() {return this.time.minutes;}

    public int getTimeSecond() {return this.time.seconds;}

    public void setTime(Time time){
        this.time = time;
    }
}
