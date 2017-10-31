package made.by.hemangini.planyourday;


import java.util.GregorianCalendar;


public class DurationTask {
    private String Note;
    private String Place;
    private GregorianCalendar Date;
    private Time timeFrom;
    private Time timeTo;

    DurationTask(){}

    DurationTask(GregorianCalendar Date, Time timeFrom, Time timeTo, String Place, String Note){
        this.Note = Note;
        this.Place = Place;
        this.Date = Date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    //get and set methods
    public GregorianCalendar getDate(){return  this.Date;}

    public void setDate(GregorianCalendar Date){ this.Date = Date;}

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

    public int getTimeFromHour(){
        return this.timeFrom.hours;
    }

    public int getTimeFromMinute() {return this.timeFrom.minutes;}

    public int getTimeFromSecond() {return this.timeFrom.seconds;}

    public void setTimeFrom(Time time){
        this.timeFrom = time;
    }

    public int getTimeToHour(){
        return this.timeTo.hours;
    }

    public int getTimeToMinute() {return this.timeTo.minutes;}

    public int getTimeToSecond() {return this.timeTo.seconds;}

    public void setTimeTo(Time time){
        this.timeTo = time;
    }
}
