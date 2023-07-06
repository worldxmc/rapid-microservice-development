package cloud.framework.page;

import cloud.framework.util.ReflectHelper;
import com.google.common.base.Preconditions;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * 分页插件
 * @author xmc
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class PrepareInterceptor implements Interceptor {

    /**
     * 要执行分页的sql结尾字符
     */
    private static String sqlTail = "";

    public PrepareInterceptor() {
    }

    /**
     * 拦截入口方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler handler = (RoutingStatementHandler)invocation.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getFieldValue(handler, "delegate");
            MappedStatement mappedStatement = (MappedStatement)ReflectHelper.getFieldValue(delegate, "mappedStatement");
            // 匹配符合拦截规则的方法
            if (mappedStatement.getId().matches(sqlTail)) {
                BoundSql boundSql = delegate.getBoundSql();
                // 校验参数是否为空
                Object parameterObject = Preconditions.checkNotNull(boundSql.getParameterObject(), "parameterObject尚未实例化！");
                // 校验参数是否为Page类型
                Preconditions.checkArgument(parameterObject instanceof Page);
                // 执行分页
                this.dataPaging(invocation, mappedStatement, boundSql, parameterObject);
            }
            // 打印sql
            printSql(delegate);
        }
        return invocation.proceed();
    }

    /**
     * 打印sql
     */
    private void printSql(BaseStatementHandler delegate) {
        System.out.println("----------------------------");
        System.out.println(delegate.getBoundSql().getSql());
        System.out.println("----------");
        System.out.println(delegate.getBoundSql().getParameterObject());
        System.out.println("----------------------------");
    }

    @Override
    public Object plugin(Object obj) {
        return Plugin.wrap(obj, this);
    }

    /**
     * 项目启动时会读取mybatis-config.xml配置文件中为当前插件设置的属性值
     */
    @Override
    public void setProperties(Properties properties) {
        sqlTail = properties.getProperty("sqlTail");
    }

    /**
     * 执行分页操作
     */
    private void dataPaging(Invocation invocation, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException, NoSuchFieldException, IllegalAccessException {
        // 获取连接对象，查询总记录数
        int count = getTotalSize(invocation, mappedStatement, boundSql, parameterObject);
        // 将总记录数赋值给参数对象的totalSize属性
        Page page = (Page)parameterObject;
        page.setTotalSize(count);

        // 拼装新的分页sql
        StringBuffer pageSql = new StringBuffer();
        pageSql.append(boundSql.getSql());
        int startNumber = (page.getPageNumber() - 1) * page.getPageSize();
        startNumber = startNumber < 0 ? 0 : startNumber;
        pageSql.append(" LIMIT " + startNumber + "," + page.getPageSize());
        // 把新的分页sql赋值给boundSql对象的sql属性
        ReflectHelper.setFieldValue(boundSql, "sql", pageSql.toString());
    }

    private int getTotalSize(Invocation invocation, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        Connection connection = (Connection)invocation.getArgs()[0];
        String countSql = "SELECT COUNT(0) FROM (" + boundSql.getSql() + ")  TMP_COUNT";
        int count = 0;
        PreparedStatement preparedStatement = connection.prepareStatement(countSql);
        try {
            BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameterObject);
            this.setParameters(preparedStatement, mappedStatement, newBoundSql, parameterObject);
            ResultSet rs = preparedStatement.executeQuery();
            try {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            } catch (Throwable throwable) {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Throwable throwable) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return count;
    }

    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for(int i = 0; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = (ParameterMapping)parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    Object value;
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith("__frch_") && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = (TypeHandler)Preconditions.checkNotNull(parameterMapping.getTypeHandler(), String.format("There was no TypeHandler found for parameter %s of statement %s", propertyName, mappedStatement.getId()));
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

}