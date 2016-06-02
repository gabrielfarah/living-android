package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.ar_smart.www.helpers.Constants;

/**
 * POJO of Trigger
 * Created by caev03 on 25/05/2016.
 */
public class Trigger implements Parcelable
{
    private int idTrigger;
    private List<Command> payload;
    private String operand;
    private int primary_value;
    private int secondary_value;
    private boolean notify;
    private int[] minute_of_day;
    private int[] days_of_the_week;
    private Endpoint endpoint;
    private int hubId;
    private Mode mode;
    private String trigger_type;

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        if(payload==null)
        {
            payload = new ArrayList<>();
        }
        if(minute_of_day==null)
        {
            minute_of_day=new int[1];
        }
        if(days_of_the_week==null)
        {
            days_of_the_week=new int[1];
        }
        dest.writeParcelable(endpoint,flags);
        dest.writeInt(hubId);
        dest.writeParcelable(mode,flags);
        dest.writeTypedList(payload);
        dest.writeString(operand);
        dest.writeInt(primary_value);
        dest.writeInt(secondary_value);
        dest.writeByte((byte)(notify?1:0));
        dest.writeIntArray(minute_of_day);
        dest.writeIntArray(days_of_the_week);
    }

    protected Trigger(Parcel in)
    {
        if(payload==null)
        {
            payload = new ArrayList<>();
        }
        if(minute_of_day==null)
        {
            minute_of_day=new int[1];
        }
        if(days_of_the_week==null)
        {
            days_of_the_week=new int[1];
        }
        endpoint = in.readParcelable(Endpoint.class.getClassLoader());
        hubId = in.readInt();
        mode = in.readParcelable(Mode.class.getClassLoader());
        in.readTypedList(payload,Command.CREATOR);
        operand = in.readString();
        primary_value = in.readInt();
        secondary_value = in.readInt();
        notify = in.readByte()!=0;
        in.readIntArray(minute_of_day);
        in.readIntArray(days_of_the_week);
    }

    public Trigger(Endpoint pEndpoint, int pHub)
    {
        endpoint = pEndpoint;
        hubId = pHub;
        payload = new ArrayList<>();
        minute_of_day = new int[1];
        days_of_the_week = new int[1];
    }


    /**
     * Returns mode field
     **/
    public List<Command> getPayload()
    {
        return payload;
    }

    /**
     * Set value for mode field
     */
    public void setPayload(List<Command> payload)
    {
        this.payload = payload;
    }

    /**
     * Returns operand field
     **/
    public String getOperand()
    {
        return operand;
    }

    /**
     * Set value for operand field
     */
    public void setOperand(String operand)
    {
        this.operand = operand;
    }

    /**
     * Returns primary_value field
     **/
    public int getPrimary_value()
    {
        return primary_value;
    }

    /**
     * Set value for primary_value field
     */
    public void setPrimary_value(int primary_value)
    {
        this.primary_value = primary_value;
    }

    /**
     * Returns secundary_value field
     **/
    public int getSecondary_value()
    {
        return secondary_value;
    }

    /**
     * Set value for secundary_value field
     */
    public void setSecondary_value(int secundary_value)
    {
        this.secondary_value = secundary_value;
    }

    /**
     * Returns notify field
     **/
    public boolean isNotify()
    {
        return notify;
    }

    /**
     * Set value for notify field
     */
    public void setNotify(boolean notify)
    {
        this.notify = notify;
    }

    /**
     * Returns minute_of_day field
     **/
    public int[] getMinute_of_day()
    {
        return minute_of_day;
    }

    /**
     * Set value for minute_of_day field
     */
    public void setMinute_of_day(int[] minute_of_day)
    {
        this.minute_of_day = minute_of_day;
    }

    /**
     * Returns days_of_the_week field
     **/
    public int[] getDays_of_the_week()
    {
        return days_of_the_week;
    }

    /**
     * Set value for days_of_the_week field
     */
    public void setDays_of_the_week(int[] days_of_the_week)
    {
        this.days_of_the_week = days_of_the_week;
    }

    /**
     * Returns trigger_type field
     **/
    public String getTrigger_type()
    {
        return trigger_type;
    }

    /**
     * Set value for trigger_type field
     */
    public void setTrigger_type(String trigger_type)
    {
        this.trigger_type = trigger_type;
    }

    /**
     * Returns idTrigger field
     **/
    public int getIdTrigger()
    {
        return idTrigger;
    }

    /**
     * Set value for idTrigger field
     */
    public void setIdTrigger(int idTrigger)
    {
        this.idTrigger = idTrigger;
    }

    /**
     * Returns mode field
     **/
    public Mode getMode()
    {
        return mode;
    }

    /**
     * Set value for mode field
     */
    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public String getDaysAsString()
    {
        Map<Integer,String> days = Constants.getHashMapDaysFromInteger();
        String result = "";
        for (int aDays_of_the_week : days_of_the_week)
        {
            result += days.get(aDays_of_the_week) + " ";
        }
        return result;
    }

    public String getHoursAsString()
    {
        String respuesta = "";
        respuesta += (minute_of_day[0]/60)+":"+(minute_of_day[0]%60)+"_";
        respuesta += (minute_of_day[1]/60)+":"+(minute_of_day[1]%60)+"_";
        return respuesta;
    }

    /**
     * Returns endpoint field
     **/
    public Endpoint getEndpoint()
    {
        return endpoint;
    }

    /**
     * Returns hubId field
     **/
    public int getHubId()
    {
        return hubId;
    }



    public static final Creator<Trigger> CREATOR = new Creator<Trigger>()
    {
        @Override
        public Trigger createFromParcel(Parcel in)
        {
            return new Trigger(in);
        }

        @Override
        public Trigger[] newArray(int size)
        {
            return new Trigger[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }



}
