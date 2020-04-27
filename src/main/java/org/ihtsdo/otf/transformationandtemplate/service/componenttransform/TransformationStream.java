package org.ihtsdo.otf.transformationandtemplate.service.componenttransform;

import java.io.IOException;

public interface TransformationStream extends AutoCloseable {

	ComponentTransformation next() throws IOException;

	@Override
	void close();
}
