package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.SurveyTemplate;

/**
 * An implementation of the SurveyTemplateDAO interface.
 * 
 * lxie
 */

@Repository
public class SurveyTemplateDAOImpl extends JdbcDaoSupport implements SurveyTemplateDAO {
	@Autowired
	public SurveyTemplateDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public SurveyTemplate getSurveyTemplateByID(int id) {
		String sql = "SELECT * FROM surveys WHERE survey_ID=?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new SurveyTemplateMapper());
	}

	@Override
	public List<SurveyTemplate> getAllSurveyTemplates() {
		String sql = "SELECT * FROM surveys ORDER BY priority DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}

	@Override
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle) {
		String sql = "SELECT * FROM surveys WHERE UPPER(title) LIKE UPPER('%" + partialTitle + "%')";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}

	@Override
	public void uploadSurveyTemplate(SurveyTemplate st) {
		String sql = "INSERT INTO surveys (title, type, contents, priority, description) values (?,?,?,?,?)";
		getJdbcTemplate().update(sql, st.getTitle(), st.getType(), st.getContents(), st.getPriority(), st.getDescription());
	}

	@Override
	public void deleteSurveyTemplate(int id) {
		String sql = "DELETE FROM surveys WHERE survey_ID=?";
		getJdbcTemplate().update(sql, id);

	}

	@Override
	public int countSurveyTemplate() {
		String sql = "SELECT COUNT(*) as c FROM surveys";
		return getJdbcTemplate().queryForInt(sql);
	}
	
	class SurveyTemplateMapper implements RowMapper<SurveyTemplate> {
		public SurveyTemplate mapRow(ResultSet rs, int rowNum) throws SQLException{
			SurveyTemplate st = new SurveyTemplate();
		
			st.setSurveyID(rs.getInt("survey_ID"));
            st.setTitle(rs.getString("title"));
            st.setType(rs.getString("type"));
            st.setContents(rs.getBytes("contents"));
            st.setPriority(rs.getInt("priority"));
            st.setDescription(rs.getString("description"));
            
            //format date, remove time
            String date = rs.getString("last_modified");
            date = date.substring(0, 10);
            st.setCreatedDate(date);
			return st;
		}
	}

}
