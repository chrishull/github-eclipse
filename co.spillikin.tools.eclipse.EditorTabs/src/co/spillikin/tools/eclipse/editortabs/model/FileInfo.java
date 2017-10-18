package co.spillikin.tools.eclipse.editortabs.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Lowest level data in the model.  Contains actual file informations
 * which is linked to the full path as a key.  Again, POJO / JABX.
 * But this class is public to the UI as the UI needs to fill 
 * in this info from Eclipse.   This allows me to avoid 
 * passing in Eclipse PDE objects here, so I'm sitll somewhat MVC.
 * 
 * @author chris
 *
 */
@XmlRootElement
public class FileInfo implements Comparable<FileInfo> {
    
    private String fileName;
    private String fullPath;
    // Provided by ITextSelection
    private Integer endLine;
    private Integer length;
    private Integer     offset;
    private Integer     startLine;
    private String  text;
    private Boolean isEmpty;
    // Adding this for future feature
    private Boolean isDirectory = false;
    
    // Serialization
    private FileInfo() {}
    
    public FileInfo (String fileName, String fullPath, Integer startLine,  
        Integer endLine, Integer offset, Integer length, String text, 
        Boolean isEmpty) {
        this.fileName = fileName;
        this.fullPath = fullPath;
        this.startLine = startLine;
        this.endLine = endLine;
        this.offset = offset;
        this.length = length;
        this.text = text;
        this.isEmpty = isEmpty;
    }
    
    /**
     * Future feature.  Manage directories as a whole.
     * @return
     */
    public Boolean getIsDirectory() {
        return isDirectory;
    }
    @XmlAttribute
    private void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }
    
    public Boolean getIsEmpty() {
        return isEmpty;
    }
    @XmlAttribute
    private void setIsEmpty(Boolean isEmpty) {
        this.isEmpty = isEmpty;
    }
    
    public String getFileName() {
        return fileName;
    }
    @XmlAttribute
    private void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFullPath() {
        return fullPath;
    }
    @XmlAttribute
    private void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
    public Integer getEndLine() {
        return endLine;
    }
    @XmlAttribute
    private void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }
    public Integer getLength() {
        return length;
    }
    @XmlAttribute
    private void setLength(Integer length) {
        this.length = length;
    }
    public Integer getOffset() {
        return offset;
    }
    @XmlAttribute
    private void setOffset(Integer offset) {
        this.offset = offset;
    }
    public Integer getStartLine() {
        return startLine;
    }
    @XmlAttribute
    private void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }
    public String getText() {
        return text;
    }
    @XmlElement
    private void setText(String text) {
        this.text = text;
    }

    // Used for alphabetize.
    @Override
    public int compareTo(FileInfo o) {
        return getFileName().compareTo(o.getFileName());
    }
}
