package local.fabricarag.core.web;

import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.repository.KnowledgeCollectionRepository;
import local.fabricarag.core.repository.WorkspaceRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

public class PublicIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final ApplicationContext applicationContext;

    public PublicIdArgumentResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ResolvePublicId.class) &&
               parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        ResolvePublicId annotation = parameter.getParameterAnnotation(ResolvePublicId.class);
        String pathVarName = annotation.pathVar();
        if (pathVarName.isEmpty()) {
            pathVarName = parameter.getParameterName();
        }
        
        Class<?> entityClass = annotation.value();

        @SuppressWarnings("unchecked")
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        String publicId = uriTemplateVars != null ? uriTemplateVars.get(pathVarName) : null;
        if (publicId == null) {
            // Also try request parameters if not found in path
            publicId = webRequest.getParameter(pathVarName);
        }
        
        if (publicId == null) {
            throw new IllegalArgumentException("Variable " + pathVarName + " not found for @ResolvePublicId");
        }

        if (entityClass == Workspace.class) {
            WorkspaceRepository repo = applicationContext.getBean(WorkspaceRepository.class);
            return repo.findByPublicId(publicId)
                    .map(Workspace::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
        } else if (entityClass == KnowledgeCollection.class) {
            KnowledgeCollectionRepository repo = applicationContext.getBean(KnowledgeCollectionRepository.class);
            return repo.findByPublicId(publicId)
                    .map(KnowledgeCollection::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found"));
        }
        
        throw new IllegalArgumentException("Unsupported entity class for public ID resolution: " + entityClass);
    }
}
