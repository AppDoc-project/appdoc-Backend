package webdoc.authentication.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ArrayResponse<T> {
    private final List<T> data;
    private final int httpStatus;
    private final int size;

    public static <R> ArrayResponse<R> of(List<R> data, int httpStatus){
       return new ArrayResponse<R>(data,httpStatus,data.size());
    }

}
