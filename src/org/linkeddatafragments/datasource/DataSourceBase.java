package org.linkeddatafragments.datasource;

/**
 *
 * @author mielvandersande
 * @author Bart Hanssens
 */
public abstract class DataSourceBase implements IDataSource {
    protected String title;
    protected String description; 
    
    public DataSourceBase(String title, String description) {
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

    @Override
    public void close() {}
}
