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
import software.amazon.awssdk.services.jsonprotocoltests.model.EventTwo;
import software.amazon.awssdk.utils.Validate;

/**
 * {@link EventTwo} Marshaller
 */
@Generated("software.amazon.awssdk:codegen")
@SdkInternalApi
public class EventTwoMarshaller implements Marshaller<EventTwo> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().hasExplicitPayloadMember(false)
            .hasPayloadMembers(true).hasImplicitPayloadMembers(true).httpMethod(SdkHttpMethod.GET).hasEvent(true).build();

    private final BaseAwsJsonProtocolFactory protocolFactory;

    public EventTwoMarshaller(BaseAwsJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public SdkHttpFullRequest marshall(EventTwo eventTwo) {
        Validate.paramNotNull(eventTwo, "eventTwo");
        try {
            ProtocolMarshaller<SdkHttpFullRequest> protocolMarshaller = protocolFactory
                    .createProtocolMarshaller(SDK_OPERATION_BINDING);
            return protocolMarshaller.marshall(eventTwo).toBuilder().putHeader(":message-type", "event")
                    .putHeader(":event-type", eventTwo.sdkEventType().toString())
                    .putHeader(":content-type", protocolFactory.getContentType()).build();
        } catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause(e).build();
        }
    }
}
