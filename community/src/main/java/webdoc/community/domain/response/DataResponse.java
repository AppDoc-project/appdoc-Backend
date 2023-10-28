package webdoc.community.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DataResponse<T> {
    private final List<T> data;
    private final int httpStatus;
    private final int size;



    public static <R> DataResponse<R> of(List<R> data,int httpStatus){
       return new DataResponse<R>(data,httpStatus,data.size());
    }

}
