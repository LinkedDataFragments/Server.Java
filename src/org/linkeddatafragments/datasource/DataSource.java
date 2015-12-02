package org.linkeddatafragments.datasource;

/**
 *
 * @author mielvandersande
 * @author Bart Hanssens
 */
public abstract class DataSource implements IDataSource {
    protected String title;
    protected String description; 

    /**
     * 
     * @param offset
     * @param limit 
     */
    protected void checkBoundaries(long offset, long limit) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("offset");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("limit");
        }
    }
    
    public DataSource(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    };

    @Override
    public String getTitle() {
        return this.title;
    };
}
