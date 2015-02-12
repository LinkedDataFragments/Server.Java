package org.linkeddatafragments.datasource;

/**
 *
 * @author mielvandersande
 */
public abstract class DataSource implements IDataSource {
    protected String title;
    protected String description; 

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
