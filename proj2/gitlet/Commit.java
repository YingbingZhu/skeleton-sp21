package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** The commit date */
    private Date timeStamp;

    /** SHA1 of parent branch(the branch you are in)*/
    private String parentSha1;

    private HashMap<String, String> blobs;

    /** The merged-in branch*/
    private String secParentSha1;

    
    /* TODO: fill in the rest of this class. */
    public Commit() {
        this.timeStamp = new Date(0);
        this.message = "initial commit";
        this.blobs = new HashMap<>();
        this.parentSha1 = "";
        this.secParentSha1 = null;
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_DIR, sha1), this);
    }

    public Commit(Date ts, String message, HashMap<String, String> blobs, String parentSha1) {
        this.timeStamp = ts;
        this.message = message;
        this.blobs = blobs;
        this.parentSha1 = parentSha1;
        this.secParentSha1 = null;
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_DIR, sha1), this);
    }

    public void setSecParentSha1(String sha1) {
        this.secParentSha1 = sha1;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getBolbs() {
        return blobs;
    }

    public String getParentSha1() {
        return parentSha1;
    }

    public String getSecParentSha1() {
        return secParentSha1;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBolbs(HashMap<String, String> bolbs) {
        this.blobs = bolbs;
    }

    public void setParentSha1(String parentSha1) {
        this.parentSha1 = parentSha1;
    }

}
