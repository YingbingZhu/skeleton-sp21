package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** current repo*/
    public static final File CURRENT_REPOSITORY = join(GITLET_DIR, "currentRepository");

    /** The .gitlet staging area*/
    public static final File STAGING_AREA_DIR = join(GITLET_DIR, "stagingArea");

    public static final File COMMIT_DIR = join(GITLET_DIR, "commitArea");
    /** The saved contents of files*/
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    private final Commit initCommit;
    private Commit HEAD;
    private String currentBranch;
    private HashMap<String, Commit> branches;
    private HashMap<String, String> stage;

    private Set<String> stageRM;

    /* TODO: fill in the rest of this class. */
    public Repository() {
        GITLET_DIR.mkdir();
        STAGING_AREA_DIR.mkdir();
        BLOB_DIR.mkdir();
        COMMIT_DIR.mkdir();

        initCommit = new Commit();
        HEAD = initCommit;
        branches = new HashMap<>();
        currentBranch = "master";
        branches.put(currentBranch, HEAD);
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    public void add(String fileName) {
        byte[] content = readContents(join(CWD, fileName));  // read content as byte array
        String sha1 = sha1(content); // get sha1 code
        stageRM.remove(fileName);
        // if file is identical to current working dir, do not stage it to be added
        if (HEAD.getBolbs().containsKey(fileName) && (HEAD.getBolbs().get(fileName).equals(sha1))){
            if (stage.containsKey(fileName)) {
                String existSha1 = stage.get(fileName);
                restrictedDelete(join(BLOB_DIR, existSha1));   // The file name is SHA1 code, delete file
                stage.remove(fileName);  // remove file from stage
            }
            return;
        }
        writeContents(join(STAGING_AREA_DIR, sha1), content);
        stage.put(fileName, sha1);
    }

    public boolean commit(String message) {
        // If no files have been staged, abort
        if (stage.size() == 0 && stageRM.size() == 0) {
            System.out.println("No changes added to the commit.");
            return false;
        }
        HashMap<String, String> addedBlobs = new HashMap<>();

        // addblobs lastCommit
        for (Map.Entry<String, String> entry: HEAD.getBolbs().entrySet()){
            addedBlobs.put(entry.getKey(), entry.getValue());
        }
        // put all files in stage
        for (Map.Entry<String, String> entry: stage.entrySet()){
            addedBlobs.put(entry.getKey(), entry.getValue());
            File stageFile = join(STAGING_AREA_DIR, entry.getValue());
            if (stageFile.exists()) {
                writeContents(join(BLOB_DIR, entry.getValue()), readContents(stageFile));
            }
        }
        // remove
        for (String rmFile: stageRM){
             addedBlobs.remove(rmFile);
        }

        Commit newCommit = new Commit(new Date(),
                message,
                addedBlobs,
                Utils.sha1(Utils.serialize(HEAD)));
        HEAD = newCommit;
        branches.put(currentBranch, newCommit);
        checkoutBranchCleanStage();
        return true;
    }

    public boolean rm(String fileName) {
        //  Unstage the file if it is currently staged for addition
        if (stage.containsKey(fileName)){
            String rmSha1 = stage.get(fileName);
            join(STAGING_AREA_DIR, rmSha1).delete();
            stage.remove(fileName);
            return true;
        }

        // stage for rm
        if (HEAD.getBolbs().containsKey(fileName)){
            stageRM.add(fileName);
            // remove the file from the working directory if the user has not already done so
            File rmFile = join(CWD, fileName);
            if (rmFile.exists()){
                rmFile.delete();
            }
            return true;
        }
        return false;
    }

    public void log() {
        Commit iterCommit = HEAD;
        while (true) {
            printLog(iterCommit);
            if (iterCommit.getParentSha1().equals("")){
                break;
            }
            File commitFile = join(COMMIT_DIR, iterCommit.getParentSha1());
            iterCommit = readObject(commitFile, Commit.class);
        }
    }

    public void globalLog() {
        Commit iterCommit;
        for (String commitFile: plainFilenamesIn(COMMIT_DIR)){
            iterCommit = readObject(join(COMMIT_DIR, commitFile), Commit.class);
            printLog(iterCommit);
        }
    }

    public void printLog(Commit commit){
        String sha1 = Utils.sha1(Utils.serialize(commit));
        System.out.println("===");
        System.out.println("commit " + sha1);
        Formatter fmt = new Formatter(Locale.ENGLISH);
        Date cal = commit.getTimeStamp();
        fmt.format("%ta %tb %td %tR:%tS %tY %tz", cal, cal, cal, cal, cal, cal, cal);
        System.out.println("Date: " + fmt);
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** Prints out the ids of all commits that have the given commit message, one per line*/
    public boolean find(String message) {
        Commit iterCommit;
        boolean res = false;
        for (String commitFile : plainFilenamesIn(COMMIT_DIR)) {
            iterCommit = readObject(join(COMMIT_DIR, commitFile), Commit.class);
            if (message.equals(iterCommit.getMessage())) {
                System.out.println(commitFile);
                res = true;
            }
        }
        return res;
    }

    public void status() {
        System.out.println("=== Branches ===");
        System.out.println("*" + currentBranch);
//        Set<String> sortBranchesSet = new TreeSet<>(Comparator.reverseOrder());
//        sortBranchesSet.addAll(branches.keySet());
        for (String branch: branches.keySet()){
            if (!branch.equals(currentBranch)) {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
//        Set<String> sortStageSet = new TreeSet<>(Comparator.reverseOrder());
//        sortStageSet.addAll(stage.keySet());
        for (String file: stage.keySet()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
//        Set<String> sortStageRMSet = new TreeSet<>(Comparator.reverseOrder());
//        sortStageRMSet.addAll(stageRM);
        for (String file: stageRM) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        Set<String> sortMNSSet = modificationNotStagedSet();
        for (String file: sortMNSSet) {
            System.out.println(file);
        }
        System.out.println();


        /** file present in working directory but neither staged for addition nor tracked*/
        System.out.println("=== Untracked Files ===");
        for (String file: plainFilenamesIn(CWD)) {
            if (!HEAD.getBolbs().containsKey(file) && !stage.containsKey(file)){
                System.out.println(file);
            }
        }
        System.out.println();
    }

    public  Set<String>  modificationNotStagedSet() {
        Set<String> sortSet = new TreeSet<>(Comparator.reverseOrder());
        for (Map.Entry<String, String> entry : HEAD.getBolbs().entrySet()) {
            File workFile = join(CWD, entry.getKey());
            // Not staged for removal, but tracked in the current commit and deleted from the working directory
            if (!workFile.exists() && !stageRM.contains(entry.getKey())) {
                sortSet.add(entry.getKey() + " (deleted)");
                continue;
            }

            // Tracked in the current commit, changed in the working directory, but not staged;
            String workFileSha1 = sha1(readContents(workFile));
            if (!workFileSha1.equals(entry.getValue()) && !stage.containsKey(entry.getKey())) {
                sortSet.add(entry.getKey() + " (modified)");
            }
        }

        for (Map.Entry<String, String> entry : stage.entrySet()) {
            // Staged for addition, but deleted in the working directory;
            File workFile = join(CWD, entry.getKey());
            if (!workFile.exists()) {
                sortSet.add(entry.getKey() + " (deleted)");
                continue;
            }

            // Staged for addition, but with different contents than in the working directory
            String workFileSha1 = sha1(readContents(workFile));
            if (!entry.getValue().equals(workFileSha1)) {
                sortSet.add(entry.getKey() + " (modified)");
            }
        }

        return sortSet;
    }

    public boolean checkout(String fileName) {
        return helpCheckout(fileName, HEAD);
    }

    public boolean checkout(String commitSha1, String fileName) {
         if (commitSha1.length()<20){
             commitSha1 = useShortId(commitSha1);
             if (commitSha1 == null) {
                 return false;
             }
         }

         // no commit with the given id exists
         File commitFile = join(COMMIT_DIR, commitSha1);
         if (!commitFile.exists()) {
             System.out.println("No commit with that id exists.");
             return false;
         }

         Commit commit = readObject(commitFile, Commit.class);
         return helpCheckout(fileName, commit);
    }

    public String useShortId(String id) {
        String fullSha1 = null;
        int len = id.length();
        for (String fileName: plainFilenamesIn(COMMIT_DIR)){
           String sub = fileName.substring(0, len);
           if (id.equals(sub)) {
               if (fullSha1 == null) {
                   fullSha1 = fileName;
               } else {
                   return null;
               }
           }
        }
        return fullSha1;
    }

    public boolean checkoutBranch(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return false;
        }
        if (branch.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            return false;
        }
        for (String file:plainFilenamesIn(CWD)){
            if (!HEAD.getBolbs().containsKey(file)) {
                String msg =  "There is an untracked file in the way; delete it, or add and commit it first.";
                System.out.println(msg);
                return false;
            }
        }

        currentBranch = branch;
        HEAD = branches.get(currentBranch);
        helpReset(HEAD);
        return true;
    }

    private void helpReset(Commit commit){
        // remove files that are not present in the commit
        for (String file:plainFilenamesIn(CWD)){
            if (!commit.getBolbs().containsKey(file)){
                File deleteFile = join(CWD, file);
                deleteFile.delete();
            }
        }
        // write blob files from commit to cwd
        for (Map.Entry<String, String> entry: commit.getBolbs().entrySet()){
            File cwdFile = join(CWD, entry.getKey());
            File blobFile = join(BLOB_DIR, entry.getValue());
            if (cwdFile.exists()){
                String cwdSha1 = sha1(readContents(cwdFile));
                if (cwdSha1.equals(entry.getValue())){
                    continue;
                }
            }
            writeContents(cwdFile, readContents(blobFile));
        }
        checkoutBranchCleanStage();
    }

    private void checkoutBranchCleanStage() {
        for (Map.Entry<String, String> entry: stage.entrySet()){
            File deleteFile = join(STAGING_AREA_DIR, entry.getValue());
            deleteFile.delete();
        }
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    private boolean helpCheckout(String fileName, Commit commit) {
        if (!commit.getBolbs().containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
            return false;
        }

        File checkoutFile = join(CWD, fileName);
        File blob = join(BLOB_DIR, commit.getBolbs().get(fileName));
        writeContents(checkoutFile, readContents(blob));
        return true;
    }

    public boolean newBranch(String branch) {
        if (branches.containsKey(branch)) {
            return false;
        }

        branches.put(branch, HEAD);
        return true;
    }

    public void rmBranch(String branchName) {
        if(!branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branches.remove(branchName);
    }

    public void reset(String commitSha1){
        File commitFile = join(COMMIT_DIR, commitSha1);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = getCommit(commitSha1);
        for (String file:plainFilenamesIn(CWD)) {
            boolean b1 = !HEAD.getBolbs().containsKey(file);
            boolean b2 = !commit.getBolbs().containsValue(sha1(readContents(join(CWD,file))));
            if (b1 && b2) {
                String msgA = "There is an untracked file in the way; delete it, ";
                String msgB = "or add and commit it first.";
                String msg = msgA + msgB;
                System.out.println(msg);
                return;
            }
        }

        helpReset(commit);
        HEAD = commit;
        branches.put(currentBranch, HEAD);
    }

    private Commit getCommit(String sha1){
        return readObject(join(COMMIT_DIR, sha1), Commit.class);
    }

    /** Merges files from the given branch into the current branch*/
    public void merge(String branchName){
        if (!branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(currentBranch)){
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (stage.size()>0 || stageRM.size()>0){
            System.out.println("You have uncommitted changes.");
            return;
        }

        for (String file: plainFilenamesIn(CWD)) {
            if (!HEAD.getBolbs().containsKey(file) && !stage.containsKey(file)) {
                String msgA = "There is an untracked file in the way; delete it, ";
                String msgB = "or add and commit it first.";
                String msg = msgA + msgB;
                System.out.println(msg);
                return;
            }
        }

        Commit branchCommit = branches.get(branchName);
        Commit currentBranchCommit = branches.get(currentBranch);
        Commit commonFatherCommit = findLatestCommonAncestor(currentBranchCommit, branchCommit);


        // If the split point is the same commit as the given branch, then we do nothing; the merge is complete
        if (sha1(serialize(commonFatherCommit)).equals(sha1(serialize(branchCommit)))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (sha1(serialize(commonFatherCommit)).equals(sha1(serialize(currentBranchCommit)))) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        boolean isConflict = processMerge(currentBranchCommit, branchCommit, commonFatherCommit);
        commit("Merged " + branchName + " into " + currentBranch + ".");

        HEAD.setSecParentSha1(sha1(serialize(branchCommit)));


        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }


    }

    /*
    *                  M1  * (Change File1 and Change File2)
                     | \
  (Change File1) A5  *   * B3 (Change File2)
                     |   |
                 A4  *   * B2
                     |   |
                 A3  *   * B1
                     |  /
                 A2  *
                     |
                 A1  *
      M1's parents
     Parent 1: A5 (Commit changed File1)
     Parent 2: B3 (Commit changed File2)
    *
    * */

    public Commit findLatestCommonAncestor(Commit A, Commit B) {
        Set<String> aFather = new HashSet<>();

        // process second father
        if (A.getSecParentSha1()!=null) {
            Commit secFather = getCommit(A.getSecParentSha1());
            aFather.add(sha1(serialize(secFather)));
            while (!secFather.getParentSha1().equals("")){
                secFather = getCommit(secFather.getParentSha1());
                aFather.add(sha1(serialize(secFather)));
            }
        }

        // process first father
        while (!A.getParentSha1().equals("")) {
            aFather.add(sha1(serialize(A)));
            A = getCommit(A.getParentSha1());
        }
        aFather.add(sha1(serialize(A)));

        while (true) {
            String bSha1 = sha1(serialize(B));
            if (aFather.contains(bSha1)){
                return getCommit(bSha1);
            }
            if (!B.getParentSha1().equals("")){
                B = getCommit(B.getParentSha1());
            } else {
                break;
            }
        }

        return null;
    }

    private boolean processMerge(Commit cCommit, Commit bCommit, Commit fCommit){
        HashMap<String, String> branchBlobs = new HashMap<>();
        branchBlobs.putAll(bCommit.getBolbs());
        HashMap<String, String> currentBlobs = new HashMap<>();
        currentBlobs.putAll(cCommit.getBolbs());
        boolean isConflict = false;

        for (Map.Entry<String, String> entry: fCommit.getBolbs().entrySet()) {
            String fileName = entry.getKey();
            String bSha1 = branchBlobs.get(fileName);
            String cSha1 = currentBlobs.get(fileName);

            if (branchBlobs.containsKey(fileName)) {
                if (!entry.getValue().equals(bSha1)) {
                    if (currentBlobs.containsKey(fileName)) {
                        if (!entry.getValue().equals(cSha1)) {
                            if (!currentBlobs.get(fileName).equals(bSha1)) {
                                conflict(cSha1, bSha1, fileName);
                                isConflict = true;
                            } else {  // a file exists in current and given branches
                                stage.put(fileName, bSha1);
                                byte[] content = readContents(join(BLOB_DIR, bSha1));
                                writeContents(join(CWD, fileName), content);
                            }
                        } else {
                            conflict(null, branchBlobs.get(fileName), fileName);
                            isConflict = true;
                        }
                    }
                } else {
                    if (currentBlobs.containsKey(fileName)) {
                        if (!entry.getValue().equals(cSha1)) {
                            conflict(cSha1, null, fileName);
                            isConflict = true;
                        } else {
                            rm(fileName);
                        }
                    }
                }
            }

            branchBlobs.remove(fileName);
            currentBlobs.remove(fileName);
        }

        for (Map.Entry<String, String> entry: branchBlobs.entrySet()) {
            String fileName = entry.getKey();
            String bSha1 = entry.getValue();
            if (currentBlobs.containsKey(fileName)) {
                if (bSha1.equals(currentBlobs.get(fileName))) {
                    stage.put(fileName, bSha1);
                    writeContents(join(CWD, fileName), readContents(join(BLOB_DIR, bSha1)));
                } else {
                    conflict(null, bSha1, fileName);
                    isConflict = true;
                }
            } else {
                stage.put(fileName, bSha1);
                writeContents(join(CWD, fileName), readContents(join(BLOB_DIR, bSha1)));
            }
        }

        return isConflict;
    }

    private void conflict(String currentFileSha1, String givenFileSha1, String fileName){
            final String headFiled = "<<<<<<< HEAD\n";
            final String currentFiled = "=======\n";
            final String givenFiled = ">>>>>>>\n";

            File mergeFile = join(CWD, fileName);

            String givenContent;
            if (givenFileSha1 == null) {
                givenContent = "";
            } else {
                givenContent = readContentsAsString(join(BLOB_DIR, givenFileSha1));
            }

            StringBuffer contents = new StringBuffer(headFiled);
            contents.append(readContentsAsString(mergeFile));
            contents.append(currentFiled);
            contents.append(givenContent);
            contents.append(givenFiled);

            String sha1 = sha1(contents.toString());
            // write to staging area
            writeContents(join(STAGING_AREA_DIR, sha1), contents.toString());
            // write to cwd
            writeContents(mergeFile, contents.toString());
            stage.put(fileName, sha1);

        }


    private void generateMyTestLog(Commit cCommit, Commit bCommit, Commit fatherCommit) {
        File log = join(GITLET_DIR, "log.txt");
        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String secFM = "null";
        if (cCommit.getSecParentSha1() != null) {
            Commit secFather = getCommit(cCommit.getSecParentSha1());
            secFM = secFather.getMessage();
        }

        String givenFathermsg = "null";
        if (bCommit.getParentSha1() != null) {
            Commit givenFather = getCommit(bCommit.getParentSha1());
            givenFathermsg = givenFather.getMessage();
        }
        StringBuffer stringBuffer = new StringBuffer(readContentsAsString(log));
        stringBuffer.append("current branch: " + cCommit.getMessage() + "\n");
        stringBuffer.append("sec father: " + secFM + "\n");
        stringBuffer.append("given branch: " + bCommit.getMessage() + "\n");
        stringBuffer.append("givenBranch Father: " + givenFathermsg + "\n");
        stringBuffer.append("common father: " + fatherCommit.getMessage() + "\n");
        stringBuffer.append("father sha1:" + sha1(serialize(fatherCommit)) + "\n\n\n");
        writeContents(log, stringBuffer.toString());
    }


    public void store() {
        Utils.writeObject(CURRENT_REPOSITORY, this);
    }

}




