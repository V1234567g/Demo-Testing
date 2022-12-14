package software.amazon.awssdk.services.jsonprotocoltests.transform;

import software.amazon.awssdk.annotations.Generated;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory;
import software.amazon.awssdk.services.jsonprotocoltests.model.InputEvent;
import software.amazon.awssdk.utils.Validate;

/**
 * {@link InputEvent} Marshaller
 */
@Generated("software.amazon.awssdk:codegen")
@SdkInternalApi
public class InputEventMarshaller implements Marshaller<InputEvent> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().hasExplicitPayloadMember(true)
            .hasPayloadMembers(true).hasImplicitPayloadMembers(false).httpMethod(SdkHttpMethod.GET).hasEvent(true).build();

    private final BaseAwsJsonProtocolFactory protocolFactory;

    public InputEventMarshaller(BaseAwsJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public SdkHttpFullRequest marshall(InputEvent inputEvent) {
        Validate.paramNotNull(inputEvent, "inputEvent");
        try {
            ProtocolMarshaller<SdkHttpFullRequest> protocolMarshaller = protocolFactory
                    .createProtocolMarshaller(SDK_OPERATION_BINDING);
            return protocolMarshaller.marshall(inputEvent).toBuilder().putHeader(":message-type", "event")
                    .putHeader(":event-type", inputEvent.sdkEventType().toString())
                    .putHeader(":content-type", "application/octet-stream").build();
        } catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause(e).build();
        }
    }
}
