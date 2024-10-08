package ke.co.apollo.health.common.domain.result;


public class DataWrapper extends Metadata implements java.io.Serializable {

  private static final long serialVersionUID = -5542245773451189527L;

  public DataWrapper() {
    super(ReturnCode.OK);
  }

  public DataWrapper(Object t) {
    super(ReturnCode.OK);
    this.data = t;
  }

  public DataWrapper(ReturnCode code) {
    super(code);
  }

  public DataWrapper(ReturnCode code, Object t) {
    super(code);
    this.data = t;
  }

  public DataWrapper(ReturnCode code, String message, Object t) {
    super(code, message);
    this.data = t;
  }

  private Object data;

  public Object getData() {
    return data;
  }

  @Override
  public String toString() {

    return "DataWrapper{" + super.toString() + " ,data=" + data + '}';
  }
}
